package com.example.simple_lolsearch.service.impl;

import com.example.simple_lolsearch.dto.AccountDto;
import com.example.simple_lolsearch.dto.LeagueEntryDto;
import com.example.simple_lolsearch.dto.PlayerProfileDto;
import com.example.simple_lolsearch.dto.PlayerProfileDto;
import com.example.simple_lolsearch.entity.PlayerEntity;
import com.example.simple_lolsearch.entity.PlayerRankEntity;
import com.example.simple_lolsearch.repository.PlayerRankRepository;
import com.example.simple_lolsearch.repository.PlayerRepository;
import com.example.simple_lolsearch.service.PlayerDataService;
import com.example.simple_lolsearch.service.SummonerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PlayerDataServiceImpl implements PlayerDataService {
    private final PlayerRepository playerRepository;
    private final PlayerRankRepository playerRankRepository;
    private final SummonerService summonerService;

    private static final Duration CACHE_DURATION = Duration.ofHours(1);

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
            PlayerEntity savedPlayer = saveOrUpdatePlayer(account, summoner, leagueEntries);

            // 3. DTO로 변환하여 반환
            return convertToPlayerProfileDto(savedPlayer);

        } catch (Exception e) {
            log.error("플레이어 프로필 조회 및 저장 실패: {}#{}", gameName, tagLine, e);
            throw new RuntimeException("플레이어 프로필 처리 중 오류가 발생했습니다", e);
        }
    }
    private PlayerEntity saveOrUpdatePlayer(AccountDto account, PlayerProfileDto summoner, List<LeagueEntryDto> leagueEntries) {
        // 1. 플레이어 정보 저장/업데이트
        PlayerEntity player = playerRepository.findByGameNameAndTagLine(account.getGameName(), account.getTagLine())
                .orElse(PlayerEntity.builder()
                        .puuid(account.getPuuid())
                        .gameName(account.getGameName())
                        .tagLine(account.getTagLine())
                        .build());

        // 소환사 정보 업데이트
        player.setSummonerId(summoner.getId());
        player.setSummonerLevel(summoner.getSummonerLevel());
        player.setProfileIconId(summoner.getProfileIconId());
        player.setRevisionDate(summoner.getRevisionDate());

        PlayerEntity savedPlayer = playerRepository.save(player);

        // 2. 기존 랭크 정보 삭제
        playerRankRepository.deleteByPlayerEntityPuuid(savedPlayer.getPuuid());

        // 3. 새로운 랭크 정보 저장
        List<PlayerRankEntity> ranks = leagueEntries.stream()
                .map(entry -> PlayerRankEntity.builder()
                        .playerEntity(savedPlayer)
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
                        .build())
                .collect(Collectors.toList());

        playerRankRepository.saveAll(ranks);
        savedPlayer.setRanks(ranks);

        log.info("플레이어 정보 저장 완료: {}#{}", savedPlayer.getGameName(), savedPlayer.getTagLine());
        return savedPlayer;
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
