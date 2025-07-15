package com.example.simple_lolsearch.service.impl;


import com.example.simple_lolsearch.dto.GameDetailDto;
import com.example.simple_lolsearch.dto.GameSummaryDto;
import com.example.simple_lolsearch.dto.MatchDetailDto;
import com.example.simple_lolsearch.dto.RuneInfo;
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
import java.util.Map;
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

    private static final Duration CACHE_DURATION = Duration.ofDays(30); // 게임 데이터는 변하지 않으므로 길게

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

            // 게임 데이터는 불변이므로 캐시 만료 체크를 생략할 수도 있음
            if (LocalDateTime.now().isBefore(cacheExpiry)) {
                return Optional.of(matchEntity);
            }
        }

        return Optional.empty();
    }

    private GameSummaryDto fetchAndSaveGameSummary(String matchId, String puuid) {
        try {
            // 1. API에서 데이터 조회
            MatchDetailDto matchDetail = summonerService.getMatchDetail(matchId);

            // 2. 데이터베이스에 저장
            MatchDetailEntity savedMatch = saveOrUpdateMatch(matchDetail);

            // 3. GameSummaryDto로 변환하여 반환
            return convertToGameSummary(savedMatch, puuid);

        } catch (Exception e) {
            log.error("게임 요약 조회 및 저장 실패: matchId={}, puuid={}", matchId, puuid, e);
            throw new RuntimeException("게임 요약 처리 중 오류가 발생했습니다", e);
        }
    }

    private GameDetailDto fetchAndSaveGameDetail(String matchId) {
        try {
            // 1. API에서 데이터 조회
            MatchDetailDto matchDetail = summonerService.getMatchDetail(matchId);

            // 2. 데이터베이스에 저장
            MatchDetailEntity savedMatch = saveOrUpdateMatch(matchDetail);

            // 3. GameDetailDto로 변환하여 반환
            return convertToGameDetail(savedMatch);

        } catch (Exception e) {
            log.error("게임 상세 조회 및 저장 실패: {}", matchId, e);
            throw new RuntimeException("게임 상세 처리 중 오류가 발생했습니다", e);
        }
    }

    private MatchDetailEntity saveOrUpdateMatch(MatchDetailDto matchDetail) {
        try {
            // 1. 매치 정보 저장/업데이트
            MatchDetailEntity match = matchDetailRepository.findById(matchDetail.getMetadata().getMatchId())
                    .orElse(MatchDetailEntity.builder()
                            .matchId(matchDetail.getMetadata().getMatchId())
                            .dataVersion(matchDetail.getMetadata().getDataVersion())
                            .build());

            // JSON 데이터 직렬화
            String participantsPuuidsJson = objectMapper.writeValueAsString(
                    matchDetail.getMetadata().getParticipants()
            );
            String participantsDataJson = objectMapper.writeValueAsString(
                    matchDetail.getInfo().getParticipants()
            );
            String teamsDataJson = objectMapper.writeValueAsString(
                    matchDetail.getInfo().getTeams()
            );

            // 게임 정보 업데이트
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

            // 검색 최적화용 필드 계산
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
            // JSON에서 참가자 데이터 복원
            List<MatchDetailDto.ParticipantDto> participants = objectMapper.readValue(
                    match.getParticipantsData(),
                    new TypeReference<List<MatchDetailDto.ParticipantDto>>() {}
            );

            // 해당 플레이어 찾기
            MatchDetailDto.ParticipantDto participant = participants.stream()
                    .filter(p -> puuid.equals(p.getPuuid()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("참가자 정보를 찾을 수 없습니다"));

            // 기본 정보 계산
            String kda = GameDataUtils.calculateKDA(participant.getKills(), participant.getDeaths(), participant.getAssists());
            int cs = GameDataUtils.calculateCS(participant);
            List<Integer> items = GameDataUtils.extractItems(participant);

            // 룬 정보 추출
            RuneInfo runeInfo = runeExtractorUtil.extractRuneInfo(participant.getPerks());

            // 시간 정보 처리
            long gameCreation = match.getGameCreation();
            String absoluteDate = timeFormatterService.formatAbsoluteDate(gameCreation);
            String relativeTime = timeFormatterService.formatRelativeTime(gameCreation);
            String detailedTime = timeFormatterService.formatDetailedTime(gameCreation);

            return GameSummaryDto.builder()
                    .matchId(match.getMatchId())
                    .championName(participant.getChampionName())
                    .kills(participant.getKills())
                    .deaths(participant.getDeaths())
                    .assists(participant.getAssists())
                    .win(participant.isWin())
                    .gameDuration(match.getGameDuration())
                    .gameMode(match.getGameMode())
                    .kda(kda)
                    .cs(cs)
                    .goldEarned(participant.getGoldEarned())
                    .visionScore(participant.getVisionScore())
                    .lane(participant.getLane())
                    .role(participant.getRole())
                    .gameCreation(gameCreation)
                    .gameDate(absoluteDate)
                    .relativeTime(relativeTime)
                    .detailedTime(detailedTime)
                    .items(items)
                    .trinket(participant.getItem6())
                    .summonerSpell1Id(participant.getSummoner1Id())
                    .summonerSpell2Id(participant.getSummoner2Id())
                    .keystoneId(runeInfo.getKeystoneId())
                    .primaryRuneTree(runeInfo.getPrimaryRuneTree())
                    .secondaryRuneTree(runeInfo.getSecondaryRuneTree())
                    .runes(runeInfo.getRunes())
                    .statRunes(runeInfo.getStatRunes())
                    .build();

        } catch (Exception e) {
            log.error("GameSummary 변환 실패: {}", match.getMatchId(), e);
            throw new RuntimeException("게임 요약 변환 중 오류가 발생했습니다", e);
        }
    }

    private GameDetailDto convertToGameDetail(MatchDetailEntity match) {
        try {
            // JSON에서 전체 데이터 복원
            List<MatchDetailDto.ParticipantDto> participants = objectMapper.readValue(
                    match.getParticipantsData(),
                    new TypeReference<List<MatchDetailDto.ParticipantDto>>() {}
            );

            List<MatchDetailDto.TeamDto> teams = objectMapper.readValue(
                    match.getTeamsData(),
                    new TypeReference<List<MatchDetailDto.TeamDto>>() {}
            );

            // MatchDetailDto 재구성
            MatchDetailDto.MetadataDto metadata = new MatchDetailDto.MetadataDto();
            metadata.setMatchId(match.getMatchId());
            metadata.setDataVersion(match.getDataVersion());

            MatchDetailDto.InfoDto info = new MatchDetailDto.InfoDto();
            info.setGameCreation(match.getGameCreation());
            info.setGameDuration(match.getGameDuration());
            info.setGameMode(match.getGameMode());
            info.setGameType(match.getGameType());
            info.setGameVersion(match.getGameVersion());
            info.setMapId(match.getMapId());
            info.setParticipants(participants);
            info.setTeams(teams);

            MatchDetailDto matchDetail = new MatchDetailDto();
            matchDetail.setMetadata(metadata);
            matchDetail.setInfo(info);

            // 기본 GameDetailDto 생성
            GameDetailDto gameDetail = gameDetailMapperService.mapToGameDetail(matchDetail);

            // 🔥 랭크 정보로 향상된 GameDetailDto 반환
            return gameDetailEnhancementService.enhanceWithRankInfo(gameDetail, matchDetail);

        } catch (Exception e) {
            log.error("GameDetail 변환 실패: {}", match.getMatchId(), e);
            throw new RuntimeException("게임 상세 변환 중 오류가 발생했습니다", e);
        }
    }

    @Scheduled(fixedRate = 7200000) // 2시간마다 실행
    public void cleanupOldCache() {
        LocalDateTime threshold = LocalDateTime.now().minus(Duration.ofDays(90)); // 90일 이전 데이터
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

        // 1. DB에서 조회
        List<MatchDetailEntity> cachedMatches = getMatchesFromDb(puuid, lastGameTime, count);

        // 2. 충분한 데이터가 있는 경우
        if (cachedMatches.size() >= count) {
            return cachedMatches.stream()
                    .map(match -> convertToGameSummary(match, puuid))
                    .collect(Collectors.toList());
        }

        // 3. 부족한 경우 API에서 추가 조회
        List<String> apiMatchIds;

        if (lastGameTime == null) {
            // 🔥 초기 로드: 최신 매치들 조회
            apiMatchIds = summonerService.getRecentMatchIds(puuid, 0, count);
        } else {
            // 🔥 더보기: 시간 기반 필터링
            apiMatchIds = getMatchIdsBeforeTime(puuid, lastGameTime, count);
        }

        return apiMatchIds.stream()
                .map(matchId -> getGameSummary(matchId, puuid))
                .sorted((a, b) -> Long.compare(b.getGameCreation(), a.getGameCreation()))
                .collect(Collectors.toList());
    }


    private List<MatchDetailEntity> getMatchesFromDb(String puuid, Long lastGameTime, int count) {
        Pageable pageable = PageRequest.of(0, count);

        if (lastGameTime != null) {
            // 특정 시간 이전 게임들
            return matchDetailRepository.findByPuuidBeforeTimeOrderByGameCreationDesc(
                    puuid, lastGameTime, pageable);
        } else {
            // 🔥 최신 게임들 (첫 로드)
            return matchDetailRepository.findByPuuidOrderByGameCreationDesc(puuid, pageable);
        }
    }

    private List<String> getMatchIdsBeforeTime(String puuid, Long lastGameTime, int count) {
        try {
            log.debug("시간 기반 매치 ID 조회: puuid={}, lastGameTime={}, count={}",
                    puuid, lastGameTime, count);

            // 🔥 null 체크 추가
            if (lastGameTime == null) {
                log.debug("lastGameTime이 null이므로 최신 매치 조회로 처리");
                return summonerService.getRecentMatchIds(puuid, 0, count);
            }

            // 기존 시간 기반 필터링 로직
            int fetchSize = Math.max(count * 3, 20);
            List<String> allMatchIds = summonerService.getRecentMatchIds(puuid, 0, fetchSize);

            List<String> filteredMatchIds = new ArrayList<>();

            for (String matchId : allMatchIds) {
                try {
                    Long gameTime = getGameCreationTime(matchId);

                    // 🔥 gameTime과 lastGameTime 모두 null 체크
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
        // 1. 캐시된 데이터가 있으면 사용
        Optional<MatchDetailEntity> cached = matchDetailRepository.findById(matchId);
        if (cached.isPresent()) {
            return cached.get().getGameCreation();
        }

        // 2. 캐시되지 않은 경우 API에서 조회
        try {
            MatchDetailDto matchDetail = summonerService.getMatchDetail(matchId);
            return matchDetail.getInfo().getGameCreation();
        } catch (Exception e) {
            log.warn("매치 상세 정보 조회 실패: {}", matchId, e);
            return null;
        }
    }
}
