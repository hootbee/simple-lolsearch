package com.example.simple_lolsearch.service.impl;

import com.example.simple_lolsearch.dto.*;
import com.example.simple_lolsearch.entity.PlayerEntity;
import com.example.simple_lolsearch.entity.PlayerRankEntity;
import com.example.simple_lolsearch.repository.PlayerRankRepository;
import com.example.simple_lolsearch.repository.PlayerRepository;
import com.example.simple_lolsearch.service.PlayerDataService;
import com.example.simple_lolsearch.service.RiotApiService;
import com.example.simple_lolsearch.service.SummonerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PlayerDataServiceImpl implements PlayerDataService {
    private final PlayerRepository playerRepository;
    private final PlayerRankRepository playerRankRepository;
    private final SummonerService summonerService;
    private final RiotApiService riotApiService;

    private static final Duration CACHE_DURATION = Duration.ofHours(1);
    private static final Duration RANK_CACHE_DURATION = Duration.ofHours(6);
    private static final String RANKED_SOLO_5x5 = "RANKED_SOLO_5x5";

    public PlayerProfileDto getPlayerProfile(String gameName, String tagLine) {
        log.info("플레이어 프로필 조회 시작: {}#{}", gameName, tagLine);

        // 1. 캐시된 데이터 확인
        Optional<PlayerEntity> cachedPlayer = getCachedPlayerIfValid(gameName, tagLine);

        if (cachedPlayer.isPresent()) {
            log.info("캐시된 데이터 사용: {}#{}", gameName, tagLine);
            return convertToPlayerProfileDto(cachedPlayer.get());
        }

        // 2. API에서 최신 데이터 조회 후 저장
        log.info("API에서 최신 데이터 조회: {}#{}", gameName, tagLine);
        return fetchAndSavePlayerProfile(gameName, tagLine);
    }

    public PlayerProfileDto refreshPlayerProfile(String gameName, String tagLine) {
        log.info("플레이어 프로필 강제 갱신: {}#{}", gameName, tagLine);
        return fetchAndSavePlayerProfile(gameName, tagLine);
    }

    @Override
    public RankInfo getRankInfoFromDbOrApi(String puuid) {
        log.debug("랭크 정보 조회 시작: puuid={}", puuid);

        Optional<PlayerEntity> playerOpt = playerRepository.findByPuuidWithRanks(puuid);

        if (playerOpt.isPresent()) {
            PlayerEntity player = playerOpt.get();
            if (!isRankDataStale(player)) {
                log.debug("캐시된 랭크 정보 사용: puuid={}", puuid);
                return convertToRankInfo(player);
            }
        }

        // API에서 최신 정보 조회
        RankInfo apiRankInfo = riotApiService.getRankInfoSafely(puuid);
        saveOrUpdatePlayerByPuuid(puuid, apiRankInfo);

        return apiRankInfo;
    }

    private Optional<PlayerEntity> getCachedPlayerIfValid(String gameName, String tagLine) {
        Optional<PlayerEntity> playerOpt = playerRepository.findByGameNameAndTagLineWithRanks(gameName, tagLine);

        if (playerOpt.isPresent()) {
            PlayerEntity playerEntity = playerOpt.get();
            LocalDateTime cacheExpiry = playerEntity.getUpdatedAt().plus(CACHE_DURATION);

            if (LocalDateTime.now().isBefore(cacheExpiry)) {
                return Optional.of(playerEntity);
            }
        }

        return Optional.empty();
    }

    private PlayerProfileDto fetchAndSavePlayerProfile(String gameName, String tagLine) {
        try {
            // 1. API에서 데이터 조회
            AccountDto account = summonerService.getAccountByRiotId(gameName, tagLine);
            String puuid = account.getPuuid();

            PlayerProfileDto summoner = summonerService.getSummonerByPuuid(puuid);
            List<LeagueEntryDto> leagueEntries = summonerService.getLeagueEntriesByPuuid(puuid);

            // 2. 데이터베이스에 저장
            PlayerEntity savedPlayer = saveOrUpdatePlayerWithRanks(account, summoner, leagueEntries);

            // 3. DTO로 변환하여 반환
            return convertToPlayerProfileDto(savedPlayer);

        } catch (Exception e) {
            log.error("플레이어 프로필 조회 및 저장 실패: {}#{}", gameName, tagLine, e);
            throw new RuntimeException("플레이어 프로필 처리 중 오류가 발생했습니다", e);
        }
    }

    private PlayerEntity saveOrUpdatePlayerByPuuid(String puuid, RankInfo rankInfo) {
        try {
            // API에서 전체 정보 조회
            AccountDto account = getAccountSafely(puuid);
            PlayerProfileDto summoner = getSummonerSafely(puuid);

            // RankInfo를 LeagueEntryDto로 변환
            List<LeagueEntryDto> leagueEntries = convertRankInfoToLeagueEntries(rankInfo);
            return saveOrUpdatePlayerWithRanks(account, summoner, leagueEntries);

        } catch (Exception e) {
            log.error("PUUID 기반 플레이어 저장 실패: {}", puuid, e);
            throw new RuntimeException("플레이어 정보 저장 중 오류가 발생했습니다", e);
        }
    }

    private PlayerEntity saveOrUpdatePlayerWithRanks(AccountDto account, PlayerProfileDto summoner, List<LeagueEntryDto> leagueEntries) {
        try {
            // 1. 플레이어 정보 저장/업데이트
            PlayerEntity player = playerRepository.findByPuuidWithRanks(account.getPuuid())
                    .orElse(PlayerEntity.builder()
                            .puuid(account.getPuuid())
                            .gameName(account.getGameName())
                            .tagLine(account.getTagLine())
                            .build());

            // 소환사 정보 업데이트
            updatePlayerInfo(player, account, summoner);

            PlayerEntity savedPlayer = playerRepository.save(player);

            // 2. 안전한 랭크 정보 업데이트
            updateRankInfoSafely(savedPlayer, leagueEntries);

            log.info("플레이어 정보 저장 완료: {}#{}", savedPlayer.getGameName(), savedPlayer.getTagLine());
            return savedPlayer;

        } catch (DataIntegrityViolationException e) {
            log.warn("PUUID 중복으로 인한 저장 실패, 기존 데이터 업데이트 시도: puuid={}", account.getPuuid());

            PlayerEntity existingPlayer = playerRepository.findByPuuidWithRanks(account.getPuuid())
                    .orElseThrow(() -> new RuntimeException("플레이어 조회 실패: " + account.getPuuid()));

            updatePlayerInfo(existingPlayer, account, summoner);
            PlayerEntity savedPlayer = playerRepository.save(existingPlayer);
            updateRankInfoSafely(savedPlayer, leagueEntries);

            return savedPlayer;
        }
    }

    /**
     * 플레이어 기본 정보 업데이트
     */
    private void updatePlayerInfo(PlayerEntity player, AccountDto account, PlayerProfileDto summoner) {
        // Account 정보 업데이트
        if (account != null) {
            player.setGameName(account.getGameName());
            player.setTagLine(account.getTagLine());
        }

        // Summoner 정보 업데이트
        if (summoner != null) {
            player.setSummonerId(summoner.getId());
            player.setSummonerLevel(summoner.getSummonerLevel());
            player.setProfileIconId(summoner.getProfileIconId());
            player.setRevisionDate(summoner.getRevisionDate());
        }
    }

    /**
     * 안전한 랭크 업데이트 (orphan deletion 문제 해결)
     */
    private void updateRankInfoSafely(PlayerEntity player, List<LeagueEntryDto> leagueEntries) {
        List<PlayerRankEntity> existingRanks = player.getRanks();
        if (existingRanks == null) {
            existingRanks = new ArrayList<>();
            player.setRanks(existingRanks);
        }

        // 기존 랭크들을 큐 타입별로 맵핑
        Map<String, PlayerRankEntity> existingRankMap = existingRanks.stream()
                .collect(Collectors.toMap(
                        PlayerRankEntity::getQueueType,
                        Function.identity()
                ));

        // 새로운 랭크 정보로 업데이트
        for (LeagueEntryDto entry : leagueEntries) {
            PlayerRankEntity existingRank = existingRankMap.get(entry.getQueueType());

            if (existingRank != null) {
                // 기존 랭크 업데이트
                updateRankEntity(existingRank, entry);
            } else {
                // 새로운 랭크 추가
                PlayerRankEntity newRank = createRankEntity(player, entry);
                existingRanks.add(newRank);
            }
        }

        // 더 이상 존재하지 않는 랭크 제거
        existingRanks.removeIf(rank ->
                leagueEntries.stream().noneMatch(entry ->
                        entry.getQueueType().equals(rank.getQueueType())
                )
        );
    }

    /**
     * 기존 랭크 엔티티 업데이트
     */
    private void updateRankEntity(PlayerRankEntity rankEntity, LeagueEntryDto entry) {
        rankEntity.setTier(entry.getTier());
        rankEntity.setRankDivision(entry.getRank());
        rankEntity.setLeaguePoints(entry.getLeaguePoints());
        rankEntity.setWins(entry.getWins());
        rankEntity.setLosses(entry.getLosses());
        rankEntity.setHotStreak(entry.isHotStreak());
        rankEntity.setVeteran(entry.isVeteran());
        rankEntity.setFreshBlood(entry.isFreshBlood());
        rankEntity.setInactive(entry.isInactive());
    }

    /**
     * 새로운 랭크 엔티티 생성
     */
    private PlayerRankEntity createRankEntity(PlayerEntity player, LeagueEntryDto entry) {
        return PlayerRankEntity.builder()
                .playerEntity(player)
                .queueType(entry.getQueueType())
                .tier(entry.getTier())
                .rankDivision(entry.getRank())
                .leaguePoints(entry.getLeaguePoints())
                .wins(entry.getWins())
                .losses(entry.getLosses())
                .hotStreak(entry.isHotStreak())
                .veteran(entry.isVeteran())
                .freshBlood(entry.isFreshBlood())
                .inactive(entry.isInactive())
                .build();
    }

    /**
     * RankInfo를 LeagueEntryDto 리스트로 변환
     */
    private List<LeagueEntryDto> convertRankInfoToLeagueEntries(RankInfo rankInfo) {
        if (rankInfo == null || "UNRANKED".equals(rankInfo.getTier())) {
            return new ArrayList<>();
        }

        LeagueEntryDto entry = LeagueEntryDto.builder()
                .queueType(rankInfo.getQueueType() != null ? rankInfo.getQueueType() : RANKED_SOLO_5x5)
                .tier(rankInfo.getTier())
                .rank(rankInfo.getRank())
                .leaguePoints(rankInfo.getLeaguePoints())
                .wins(rankInfo.getWins())
                .losses(rankInfo.getLosses())
                .hotStreak(false)
                .veteran(false)
                .freshBlood(false)
                .inactive(false)
                .build();

        return Arrays.asList(entry);
    }

    /**
     * 안전한 API 호출 메서드들
     */
    private AccountDto getAccountSafely(String puuid) {
        try {
            return riotApiService.getAccountByPuuid(puuid);
        } catch (Exception e) {
            log.warn("계정 정보 조회 실패, 기본값 사용: puuid={}", puuid, e);
            return AccountDto.builder()
                    .puuid(puuid)
                    .gameName("Unknown")
                    .tagLine("Unknown")
                    .build();
        }
    }

    private PlayerProfileDto getSummonerSafely(String puuid) {
        try {
            return riotApiService.getSummoner(puuid);
        } catch (Exception e) {
            log.warn("소환사 정보 조회 실패, 기본값 사용: puuid={}", puuid, e);
            return PlayerProfileDto.builder()
                    .id("unknown")
                    .summonerLevel(1)
                    .profileIconId(0)
                    .revisionDate(System.currentTimeMillis())
                    .build();
        }
    }

    private PlayerProfileDto convertToPlayerProfileDto(PlayerEntity player) {
        // AccountDto 재구성
        AccountDto account = AccountDto.builder()
                .puuid(player.getPuuid())
                .gameName(player.getGameName())
                .tagLine(player.getTagLine())
                .build();

        // LeagueEntryDto 리스트 재구성
        List<LeagueEntryDto> leagueEntries = player.getRanks().stream()
                .map(rank -> LeagueEntryDto.builder()
                        .queueType(rank.getQueueType())
                        .tier(rank.getTier())
                        .rank(rank.getRankDivision())
                        .leaguePoints(rank.getLeaguePoints())
                        .wins(rank.getWins())
                        .losses(rank.getLosses())
                        .hotStreak(rank.getHotStreak())
                        .veteran(rank.getVeteran())
                        .freshBlood(rank.getFreshBlood())
                        .inactive(rank.getInactive())
                        .build())
                .collect(Collectors.toList());

        return PlayerProfileDto.builder()
                .account(account)
                .leagueEntries(leagueEntries)
                .summonerId(player.getSummonerId())
                .profileIconId(player.getProfileIconId())
                .revisionDate(player.getRevisionDate())
                .summonerLevel(player.getSummonerLevel())
                .build();
    }

    private boolean isRankDataStale(PlayerEntity player) {
        if (player.getUpdatedAt() == null) return true;
        LocalDateTime cacheExpiry = player.getUpdatedAt().plus(RANK_CACHE_DURATION);
        return LocalDateTime.now().isAfter(cacheExpiry);
    }

    private RankInfo convertToRankInfo(PlayerEntity player) {
        Optional<PlayerRankEntity> soloRankOpt = player.getRanks().stream()
                .filter(rank -> RANKED_SOLO_5x5.equals(rank.getQueueType()))
                .findFirst();

        if (soloRankOpt.isPresent()) {
            PlayerRankEntity soloRank = soloRankOpt.get();
            String fullRankString = createFullRankString(soloRank.getTier(), soloRank.getRankDivision(), soloRank.getLeaguePoints());

            return RankInfo.builder()
                    .tier(soloRank.getTier())
                    .rank(soloRank.getRankDivision())
                    .leaguePoints(soloRank.getLeaguePoints())
                    .queueType(soloRank.getQueueType())
                    .fullRankString(fullRankString)
                    .wins(soloRank.getWins() != null ? soloRank.getWins() : 0)
                    .losses(soloRank.getLosses() != null ? soloRank.getLosses() : 0)
                    .build();
        }

        return RankInfo.createUnrankedInfo();
    }

    /**
     * fullRankString 생성 헬퍼 메서드
     */
    private String createFullRankString(String tier, String rank, Integer leaguePoints) {
        if ("UNRANKED".equals(tier) || tier == null) {
            return "언랭크";
        }

        String tierKorean = RankInfo.translateTierToKorean(tier);
        int lp = leaguePoints != null ? leaguePoints : 0;

        if (rank == null || rank.isEmpty()) {
            return String.format("%s %dLP", tierKorean, lp);
        }

        return String.format("%s %s %dLP", tierKorean, rank, lp);
    }

    @Scheduled(fixedRate = 3600000) // 1시간마다 실행
    public void cleanupOldCache() {
        LocalDateTime threshold = LocalDateTime.now().minus(Duration.ofDays(7));
        List<PlayerEntity> oldPlayers = playerRepository.findPlayersNeedingUpdate(threshold);

        if (!oldPlayers.isEmpty()) {
            log.info("오래된 캐시 데이터 정리: {} 건", oldPlayers.size());
            playerRepository.deleteAll(oldPlayers);
        }
    }
}
