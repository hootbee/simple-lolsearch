package com.example.simple_lolsearch.service.impl;

import com.example.simple_lolsearch.dto.AccountDto;
import com.example.simple_lolsearch.dto.PlayerProfileDto;
import com.example.simple_lolsearch.dto.RankInfo;
import com.example.simple_lolsearch.entity.PlayerEntity;
import com.example.simple_lolsearch.entity.PlayerRankEntity;
import com.example.simple_lolsearch.repository.PlayerRankRepository;
import com.example.simple_lolsearch.repository.PlayerRepository;
import com.example.simple_lolsearch.service.PlayerService;
import com.example.simple_lolsearch.service.RiotApiService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerRankRepository playerRankRepository;
    private final RiotApiService riotApiService;

    private static final Duration RANK_CACHE_DURATION = Duration.ofHours(6);
    private static final String RANKED_SOLO_5x5 = "RANKED_SOLO_5x5";

    @Override
    public RankInfo getRankInfoFromDbOrApi(String puuid) {
        log.debug("랭크 정보 조회 시작: puuid={}", puuid);

        // 1. DB에서 플레이어 정보 조회
        Optional<PlayerEntity> playerOpt = playerRepository.findByPuuidWithRanks(puuid);

        if (playerOpt.isPresent()) {
            PlayerEntity player = playerOpt.get();

            // 2. 캐시된 데이터가 유효한지 확인
            if (!isPlayerDataStale(player)) {
                log.debug("캐시된 랭크 정보 사용: puuid={}", puuid);
                return convertToRankInfo(player);
            }

            log.debug("캐시된 랭크 정보가 만료됨, API에서 갱신: puuid={}", puuid);
        } else {
            log.debug("DB에 플레이어 정보 없음, API에서 조회: puuid={}", puuid);
        }

        // 3. API에서 최신 랭크 정보 조회
        RankInfo apiRankInfo = riotApiService.getRankInfoSafely(puuid);

        // 4. DB에 저장/업데이트
        saveOrUpdatePlayer(puuid, apiRankInfo);

        return apiRankInfo;
    }

    @Override
    public PlayerEntity saveOrUpdatePlayer(String puuid, RankInfo rankInfo) {
        try {
            // 🔥 기존 RiotApiService 메서드들 활용
            PlayerProfileDto playerProfile = getSummonerSafely(puuid);
            AccountDto accountInfo = getAccountSafely(puuid);

            // 기존 플레이어 조회 또는 새로 생성
            PlayerEntity player = playerRepository.findByPuuid(puuid)
                    .map(existingPlayer -> updatePlayerInfo(existingPlayer, playerProfile, accountInfo))
                    .orElseGet(() -> createNewPlayer(puuid, playerProfile, accountInfo));

            // 기존 랭크 정보 삭제
            if (player.getId() != null) {
                playerRankRepository.deleteByPlayerEntityId(player.getId());
            }

            // 플레이어 정보 저장
            PlayerEntity savedPlayer = playerRepository.save(player);

            // 새로운 랭크 정보 저장
            saveRankInfo(savedPlayer, rankInfo);

            log.info("플레이어 정보 저장 완료: puuid={}, gameName={}, tier={}",
                    puuid, savedPlayer.getGameName(), rankInfo != null ? rankInfo.getTier() : "UNRANKED");
            return savedPlayer;

        } catch (DataIntegrityViolationException e) {
            // 중복 키 에러 처리
            log.warn("PUUID 중복으로 인한 저장 실패, 기존 데이터 업데이트 시도: puuid={}", puuid);

            PlayerEntity existingPlayer = playerRepository.findByPuuid(puuid)
                    .orElseThrow(() -> new RuntimeException("플레이어 조회 실패: " + puuid));

            // 전체 정보 다시 조회하여 업데이트
            PlayerProfileDto playerProfile = getSummonerSafely(puuid);
            AccountDto accountInfo = getAccountSafely(puuid);

            PlayerEntity updatedPlayer = updatePlayerInfo(existingPlayer, playerProfile, accountInfo);
            PlayerEntity savedPlayer = playerRepository.save(updatedPlayer);
            saveRankInfo(savedPlayer, rankInfo);

            return savedPlayer;

        } catch (Exception e) {
            log.error("플레이어 정보 저장 실패: puuid={}", puuid, e);
            throw new RuntimeException("플레이어 정보 저장 중 오류가 발생했습니다", e);
        }
    }

    /**
     * 🔥 기존 getSummoner 메서드 안전 호출
     */
    private PlayerProfileDto getSummonerSafely(String puuid) {
        try {
            return riotApiService.getSummoner(puuid);
        } catch (Exception e) {
            log.warn("소환사 정보 조회 실패, 기본값 사용: puuid={}", puuid, e);
            return null;
        }
    }

    /**
     * 🔥 기존 getAccountByPuuid 메서드 안전 호출
     */
    private AccountDto getAccountSafely(String puuid) {
        try {
            return riotApiService.getAccountByPuuid(puuid);
        } catch (Exception e) {
            log.warn("계정 정보 조회 실패, 기본값 사용: puuid={}", puuid, e);
            return null;
        }
    }

    /**
     * 새로운 플레이어 생성
     */
    private PlayerEntity createNewPlayer(String puuid, PlayerProfileDto playerProfile, AccountDto accountInfo) {
        return PlayerEntity.builder()
                .puuid(puuid)
                .gameName(accountInfo != null ? accountInfo.getGameName() : "Unknown")
                .tagLine(accountInfo != null ? accountInfo.getTagLine() : "Unknown")
                .summonerId(playerProfile != null ? playerProfile.getId() : null)
                .summonerLevel(playerProfile != null ? playerProfile.getSummonerLevel() : null)
                .profileIconId(playerProfile != null ? playerProfile.getProfileIconId() : null)
                .revisionDate(playerProfile != null ? playerProfile.getRevisionDate() : null)
                .build();
    }

    /**
     * 기존 플레이어 정보 업데이트
     */
    private PlayerEntity updatePlayerInfo(PlayerEntity existingPlayer, PlayerProfileDto playerProfile, AccountDto accountInfo) {
        // Account 정보 업데이트
        if (accountInfo != null) {
            if (accountInfo.getGameName() != null) {
                existingPlayer.setGameName(accountInfo.getGameName());
            }
            if (accountInfo.getTagLine() != null) {
                existingPlayer.setTagLine(accountInfo.getTagLine());
            }
        }

        // Summoner 정보 업데이트
        if (playerProfile != null) {
            if (playerProfile.getId() != null) {
                existingPlayer.setSummonerId(playerProfile.getId());
            }
            if (playerProfile.getSummonerLevel() != null) {
                existingPlayer.setSummonerLevel(playerProfile.getSummonerLevel());
            }
            if (playerProfile.getProfileIconId() != null) {
                existingPlayer.setProfileIconId(playerProfile.getProfileIconId());
            }
            if (playerProfile.getRevisionDate() != null) {
                existingPlayer.setRevisionDate(playerProfile.getRevisionDate());
            }
        }

        return existingPlayer;
    }

    /**
     * 랭크 정보 저장
     */
    private void saveRankInfo(PlayerEntity player, RankInfo rankInfo) {
        if (rankInfo != null && !rankInfo.getTier().equals("UNRANKED")) {
            PlayerRankEntity rankEntity = PlayerRankEntity.builder()
                    .playerEntity(player)
                    .queueType(rankInfo.getQueueType() != null ? rankInfo.getQueueType() : RANKED_SOLO_5x5)
                    .tier(rankInfo.getTier())
                    .rankDivision(rankInfo.getRank())
                    .leaguePoints(rankInfo.getLeaguePoints())
                    .wins(rankInfo.getWins())
                    .losses(rankInfo.getLosses())
                    .build();

            playerRankRepository.save(rankEntity);
        }
    }

    @Override
    public boolean isPlayerDataStale(PlayerEntity player) {
        if (player.getUpdatedAt() == null) {
            return true;
        }

        LocalDateTime cacheExpiry = player.getUpdatedAt().plus(RANK_CACHE_DURATION);
        return LocalDateTime.now().isAfter(cacheExpiry);
    }

    private RankInfo convertToRankInfo(PlayerEntity player) {
        // 솔로 랭크 정보 찾기
        Optional<PlayerRankEntity> soloRankOpt = player.getRanks().stream()
                .filter(rank -> RANKED_SOLO_5x5.equals(rank.getQueueType()))
                .findFirst();

        if (soloRankOpt.isPresent()) {
            PlayerRankEntity soloRank = soloRankOpt.get();

            // fullRankString 생성
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

        // 랭크 정보가 없으면 UNRANKED 반환
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
}
