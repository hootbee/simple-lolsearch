package com.example.simple_lolsearch.service.impl;

import com.example.simple_lolsearch.dto.common.*;
import com.example.simple_lolsearch.dto.match.GameDetailDto;
import com.example.simple_lolsearch.dto.match.GameSummaryDto;
import com.example.simple_lolsearch.dto.match.MatchDetailDto;
import com.example.simple_lolsearch.entity.MatchDetailEntity;
import com.example.simple_lolsearch.repository.MatchDetailRepository;
import com.example.simple_lolsearch.service.*;
import com.example.simple_lolsearch.util.GameDataUtils;
import com.example.simple_lolsearch.util.RuneExtractorUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MatchDetailServiceImpl implements MatchDetailService {

    private final MatchDetailRepository matchDetailRepository;
    private final SummonerService summonerService;
    private final GameDetailMapperService gameDetailMapperService;
    private final TimeFormatterService timeFormatterService;
    private final RuneExtractorUtil runeExtractorUtil;
    private final ObjectMapper objectMapper;
    private final GameDetailEnhancementService gameDetailEnhancementService;

    private static final Duration CACHE_DURATION = Duration.ofDays(30);

    @Override
    public GameSummaryDto getGameSummary(String matchId, String puuid) {
        log.info("게임 요약 조회 시작: matchId={}, puuid={}", matchId, puuid);

        // 1. 캐시된 데이터 확인
        Optional<MatchDetailEntity> cachedMatch = getCachedMatchIfValid(matchId);

        if (cachedMatch.isPresent()) {
            log.info("캐시된 매치 데이터 사용: {}", matchId);
            return convertToGameSummary(cachedMatch.get(), puuid);
        }

        // 2. API에서 최신 데이터 조회 후 저장
        log.info("API에서 최신 매치 데이터 조회: {}", matchId);
        return fetchAndSaveGameSummary(matchId, puuid);
    }

    @Override
    public GameDetailDto getGameDetail(String matchId) {
        log.info("게임 상세 조회 시작: {}", matchId);

        // 1. 캐시된 데이터 확인
        Optional<MatchDetailEntity> cachedMatch = getCachedMatchIfValid(matchId);

        if (cachedMatch.isPresent()) {
            log.info("캐시된 매치 상세 데이터 사용: {}", matchId);
            return convertToGameDetail(cachedMatch.get());
        }

        // 2. API에서 최신 데이터 조회 후 저장
        log.info("API에서 최신 매치 상세 데이터 조회: {}", matchId);
        return fetchAndSaveGameDetail(matchId);
    }

    @Override
    public GameDetailDto refreshGameDetail(String matchId) {
        log.info("게임 상세 강제 갱신: {}", matchId);
        return fetchAndSaveGameDetail(matchId);
    }

    private Optional<MatchDetailEntity> getCachedMatchIfValid(String matchId) {
        Optional<MatchDetailEntity> matchOpt = matchDetailRepository.findById(matchId);

        if (matchOpt.isPresent()) {
            MatchDetailEntity matchEntity = matchOpt.get();
            LocalDateTime cacheExpiry = matchEntity.getUpdatedAt().plus(CACHE_DURATION);

            if (LocalDateTime.now().isBefore(cacheExpiry)) {
                return Optional.of(matchEntity);
            }
        }

        return Optional.empty();
    }

    private GameSummaryDto fetchAndSaveGameSummary(String matchId, String puuid) {
        try {
            MatchDetailDto matchDetail = summonerService.getMatchDetail(matchId);
            MatchDetailEntity savedMatch = saveOrUpdateMatch(matchDetail);
            return convertToGameSummary(savedMatch, puuid);

        } catch (Exception e) {
            log.error("게임 요약 조회 및 저장 실패: matchId={}, puuid={}", matchId, puuid, e);
            throw new RuntimeException("게임 요약 처리 중 오류가 발생했습니다", e);
        }
    }

    private GameDetailDto fetchAndSaveGameDetail(String matchId) {
        try {
            MatchDetailDto matchDetail = summonerService.getMatchDetail(matchId);
            MatchDetailEntity savedMatch = saveOrUpdateMatch(matchDetail);
            return convertToGameDetail(savedMatch);

        } catch (Exception e) {
            log.error("게임 상세 조회 및 저장 실패: {}", matchId, e);
            throw new RuntimeException("게임 상세 처리 중 오류가 발생했습니다", e);
        }
    }

    private MatchDetailEntity saveOrUpdateMatch(MatchDetailDto matchDetail) {
        try {
            MatchDetailEntity match = matchDetailRepository.findById(matchDetail.getMetadata().getMatchId())
                    .orElse(MatchDetailEntity.builder()
                            .matchId(matchDetail.getMetadata().getMatchId())
                            .dataVersion(matchDetail.getMetadata().getDataVersion())
                            .build());

            String participantsPuuidsJson = objectMapper.writeValueAsString(
                    matchDetail.getMetadata().getParticipants()
            );
            String participantsDataJson = objectMapper.writeValueAsString(
                    matchDetail.getInfo().getParticipants()
            );
            String teamsDataJson = objectMapper.writeValueAsString(
                    matchDetail.getInfo().getTeams()
            );

            match.setParticipantsPuuids(participantsPuuidsJson);
            match.setGameCreation(matchDetail.getInfo().getGameCreation());
            match.setGameDuration(matchDetail.getInfo().getGameDuration());
            match.setGameMode(matchDetail.getInfo().getGameMode());
            match.setGameType(matchDetail.getInfo().getGameType());
            match.setGameVersion(matchDetail.getInfo().getGameVersion());
            match.setMapId(matchDetail.getInfo().getMapId());
            match.setQueueId(matchDetail.getInfo().getQueueId());
            match.setParticipantsData(participantsDataJson);
            match.setTeamsData(teamsDataJson);

            int totalKills = matchDetail.getInfo().getParticipants().stream()
                    .mapToInt(MatchDetailDto.ParticipantDto::getKills)
                    .sum();
            match.setTotalKills(totalKills);

            MatchDetailEntity savedMatch = matchDetailRepository.save(match);
            log.info("매치 정보 저장 완료: {}", savedMatch.getMatchId());
            return savedMatch;

        } catch (Exception e) {
            log.error("매치 데이터 저장 실패: {}", matchDetail.getMetadata().getMatchId(), e);
            throw new RuntimeException("매치 데이터 저장 중 오류가 발생했습니다", e);
        }
    }

    private GameSummaryDto convertToGameSummary(MatchDetailEntity match, String puuid) {
        try {
            List<MatchDetailDto.ParticipantDto> participants = objectMapper.readValue(
                    match.getParticipantsData(),
                    new TypeReference<List<MatchDetailDto.ParticipantDto>>() {}
            );

            MatchDetailDto.ParticipantDto participant = participants.stream()
                    .filter(p -> puuid.equals(p.getPuuid()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("참가자 정보를 찾을 수 없습니다"));

            // GameDetailMapperService를 사용하여 PlayerDetailDto 생성
            GameDetailDto.PlayerDetailDto playerDetail = gameDetailMapperService.mapToPlayerDetail(participant);

            // 공통 클래스들 생성
            BaseGameInfo gameInfo = createBaseGameInfo(match);

            // 시간 정보 처리
            long gameCreation = match.getGameCreation();
            String absoluteDate = timeFormatterService.formatAbsoluteDate(gameCreation);
            String relativeTime = timeFormatterService.formatRelativeTime(gameCreation);
            String detailedTime = timeFormatterService.formatDetailedTime(gameCreation);

            return GameSummaryDto.builder()
                    .gameInfo(gameInfo)
                    .playerInfo(playerDetail.getPlayerInfo())
                    .gameStats(playerDetail.getGameStats())
                    .itemSpellInfo(playerDetail.getItemSpellInfo())
                    .runeInfo(playerDetail.getRuneInfo())
                    .kda(playerDetail.getKda())
                    .cs(playerDetail.getCs())
                    .gameDate(absoluteDate)
                    .relativeTime(relativeTime)
                    .detailedTime(detailedTime)
                    .build();

        } catch (Exception e) {
            log.error("GameSummary 변환 실패: {}", match.getMatchId(), e);
            throw new RuntimeException("게임 요약 변환 중 오류가 발생했습니다", e);
        }
    }

    // createBaseGameInfo 메서드 수정 (participant 파라미터 제거)
    private BaseGameInfo createBaseGameInfo(MatchDetailEntity match) {
        return BaseGameInfo.builder()
                .matchId(match.getMatchId())
                .gameDuration(match.getGameDuration())
                .gameMode(match.getGameMode())
                .gameType(match.getGameType())
                .gameCreation(match.getGameCreation())
                .mapId(match.getMapId())
                .queueId(match.getQueueId())
                .build();
    }


    private GameDetailDto convertToGameDetail(MatchDetailEntity match) {
        try {
            List<MatchDetailDto.ParticipantDto> participants = objectMapper.readValue(
                    match.getParticipantsData(),
                    new TypeReference<List<MatchDetailDto.ParticipantDto>>() {}
            );

            List<MatchDetailDto.TeamDto> teams = objectMapper.readValue(
                    match.getTeamsData(),
                    new TypeReference<List<MatchDetailDto.TeamDto>>() {}
            );

            List<String> participantsPuuids = objectMapper.readValue(
                    match.getParticipantsPuuids(),
                    new TypeReference<List<String>>() {}
            );

            // MatchDetailDto 재구성
            MatchDetailDto.MetadataDto metadata = new MatchDetailDto.MetadataDto();
            metadata.setMatchId(match.getMatchId());
            metadata.setDataVersion(match.getDataVersion());
            metadata.setParticipants(participantsPuuids);

            MatchDetailDto.InfoDto info = new MatchDetailDto.InfoDto();
            info.setGameCreation(match.getGameCreation());
            info.setGameDuration(match.getGameDuration());
            info.setGameMode(match.getGameMode());
            info.setGameType(match.getGameType());
            info.setGameVersion(match.getGameVersion());
            info.setMapId(match.getMapId());
            info.setQueueId(match.getQueueId());
            info.setParticipants(participants);
            info.setTeams(teams);

            MatchDetailDto matchDetail = new MatchDetailDto();
            matchDetail.setMetadata(metadata);
            matchDetail.setInfo(info);

            GameDetailDto gameDetail = gameDetailMapperService.mapToGameDetail(matchDetail);
            return gameDetailEnhancementService.enhanceWithRankInfo(gameDetail, matchDetail);

        } catch (Exception e) {
            log.error("GameDetail 변환 실패: {}", match.getMatchId(), e);
            throw new RuntimeException("게임 상세 변환 중 오류가 발생했습니다", e);
        }
    }

    // === 공통 클래스 생성 메서드들 ===

    // === 스케줄러 및 페이징 메서드들 ===

    @Scheduled(fixedRate = 7200000) // 2시간마다 실행
    public void cleanupOldCache() {
        LocalDateTime threshold = LocalDateTime.now().minus(Duration.ofDays(90));
        List<MatchDetailEntity> oldMatches = matchDetailRepository.findMatchesNeedingUpdate(threshold);

        if (!oldMatches.isEmpty()) {
            log.info("오래된 매치 캐시 데이터 정리: {} 건", oldMatches.size());
            matchDetailRepository.deleteAll(oldMatches);
        }
    }

    @Override
    public List<GameSummaryDto> getGameSummaries(List<String> matchIds, String puuid) {
        log.info("게임 요약 일괄 조회: {} 건, puuid={}", matchIds.size(), puuid);

        return matchIds.stream()
                .map(matchId -> getGameSummary(matchId, puuid))
                .collect(Collectors.toList());
    }

    @Override
    public List<GameSummaryDto> getGameHistoryWithPagination(String puuid, int start, int count) {
        return null;
    }

    @Override
    public List<GameSummaryDto> getGameHistory(String puuid, Long lastGameTime, int count) {
        log.debug("게임 히스토리 조회: puuid={}, lastGameTime={}, count={}",
                puuid, lastGameTime, count);

        List<MatchDetailEntity> cachedMatches = getMatchesFromDb(puuid, lastGameTime, count);

        if (cachedMatches.size() >= count) {
            return cachedMatches.stream()
                    .map(match -> convertToGameSummary(match, puuid))
                    .collect(Collectors.toList());
        }

        List<String> apiMatchIds;

        if (lastGameTime == null) {
            apiMatchIds = summonerService.getRecentMatchIds(puuid, 0, count);
        } else {
            apiMatchIds = getMatchIdsBeforeTime(puuid, lastGameTime, count);
        }

        return apiMatchIds.stream()
                .map(matchId -> getGameSummary(matchId, puuid))
                .sorted((a, b) -> Long.compare(b.getGameInfo().getGameCreation(), a.getGameInfo().getGameCreation()))
                .collect(Collectors.toList());
    }

    private List<MatchDetailEntity> getMatchesFromDb(String puuid, Long lastGameTime, int count) {
        Pageable pageable = PageRequest.of(0, count);

        if (lastGameTime != null) {
            return matchDetailRepository.findByPuuidBeforeTimeOrderByGameCreationDesc(
                    puuid, lastGameTime, pageable);
        } else {
            return matchDetailRepository.findByPuuidOrderByGameCreationDesc(puuid, pageable);
        }
    }

    private List<String> getMatchIdsBeforeTime(String puuid, Long lastGameTime, int count) {
        try {
            log.debug("시간 기반 매치 ID 조회: puuid={}, lastGameTime={}, count={}",
                    puuid, lastGameTime, count);

            if (lastGameTime == null) {
                log.debug("lastGameTime이 null이므로 최신 매치 조회로 처리");
                return summonerService.getRecentMatchIds(puuid, 0, count);
            }

            int fetchSize = Math.max(count * 3, 20);
            List<String> allMatchIds = summonerService.getRecentMatchIds(puuid, 0, fetchSize);

            List<String> filteredMatchIds = new ArrayList<>();

            for (String matchId : allMatchIds) {
                try {
                    Long gameTime = getGameCreationTime(matchId);

                    if (gameTime != null && gameTime < lastGameTime) {
                        filteredMatchIds.add(matchId);

                        if (filteredMatchIds.size() >= count) {
                            break;
                        }
                    }
                } catch (Exception e) {
                    log.warn("매치 시간 확인 실패: {}", matchId, e);
                }
            }

            return filteredMatchIds;

        } catch (Exception e) {
            log.error("시간 기반 매치 ID 조회 실패: puuid={}, lastGameTime={}", puuid, lastGameTime, e);
            return new ArrayList<>();
        }
    }

    /**
     * 매치의 게임 생성 시간 조회 (캐시 우선)
     */
    private Long getGameCreationTime(String matchId) {
        Optional<MatchDetailEntity> cached = matchDetailRepository.findById(matchId);
        if (cached.isPresent()) {
            return cached.get().getGameCreation();
        }

        try {
            MatchDetailDto matchDetail = summonerService.getMatchDetail(matchId);
            return matchDetail.getInfo().getGameCreation();
        } catch (Exception e) {
            log.warn("매치 상세 정보 조회 실패: {}", matchId, e);
            return null;
        }
    }
}
