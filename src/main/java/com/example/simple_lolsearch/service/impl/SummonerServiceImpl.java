package com.example.simple_lolsearch.service.impl;

import com.example.simple_lolsearch.dto.*;
import com.example.simple_lolsearch.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SummonerServiceImpl implements SummonerService {

    private final RiotApiService riotApiService;
    private final GameDataMapperService gameDataMapperService;
    private final GameDetailMapperService gameDetailMapperService;

    @Override
    public AccountDto getAccountByRiotId(String gameName, String tagLine) {
        return riotApiService.getAccount(gameName, tagLine);
    }

    @Override
    public List<String> getRecentMatchIds(String puuid, int count) {
        return riotApiService.getMatchIds(puuid, count);
    }

    @Override
    public MatchDetailDto getMatchDetail(String matchId) {
        return riotApiService.getMatchDetail(matchId);
    }

    @Override
    public GameSummaryDto convertToGameSummary(MatchDetailDto match, String puuid) {
        return gameDataMapperService.mapToGameSummary(match, puuid);
    }

    @Override
    public List<LeagueEntryDto> getLeagueEntriesByPuuid(String puuid) {
        return riotApiService.getLeagueEntries(puuid);
    }

    @Override
    public SummonerDto getSummonerByPuuid(String puuid) {
        return riotApiService.getSummoner(puuid);
    }
    @Override
    public GameDetailDto getGameDetail(String matchId) {
        log.debug("게임 상세 분석 요청: {}", matchId);

        try {
            // 1. 매치 상세 정보 조회
            MatchDetailDto matchDetail = riotApiService.getMatchDetail(matchId);

            // 2. 기본 게임 상세 분석 데이터로 변환 (랭크 정보 없이)
            GameDetailDto gameDetail = gameDetailMapperService.mapToGameDetail(matchDetail);

            // 3. 각 플레이어의 랭크 정보 조회 및 업데이트
            GameDetailDto enhancedGameDetail = enhanceWithRankInfo(gameDetail, matchDetail);

            log.debug("게임 상세 분석 완료: {}", matchId);
            return enhancedGameDetail;

        } catch (Exception e) {
            log.error("게임 상세 분석 실패: {}", matchId, e);
            throw new RuntimeException("게임 상세 분석 중 오류가 발생했습니다: " + matchId, e);
        }
    }

    private GameDetailDto enhanceWithRankInfo(GameDetailDto gameDetail, MatchDetailDto matchDetail) {
        // 블루팀 플레이어 랭크 정보 업데이트
        List<GameDetailDto.PlayerDetailDto> enhancedBluePlayers = enhancePlayersWithRank(
                gameDetail.getBlueTeam().getPlayers(),
                matchDetail.getInfo().getParticipants().stream()
                        .filter(p -> p.getTeamId() == 100)
                        .collect(Collectors.toList())
        );

        // 레드팀 플레이어 랭크 정보 업데이트
        List<GameDetailDto.PlayerDetailDto> enhancedRedPlayers = enhancePlayersWithRank(
                gameDetail.getRedTeam().getPlayers(),
                matchDetail.getInfo().getParticipants().stream()
                        .filter(p -> p.getTeamId() == 200)
                        .collect(Collectors.toList())
        );

        // 업데이트된 플레이어 정보로 팀 정보 재구성
        GameDetailDto.TeamDetailDto enhancedBlueTeam = gameDetail.getBlueTeam().toBuilder()
                .players(enhancedBluePlayers)
                .build();

        GameDetailDto.TeamDetailDto enhancedRedTeam = gameDetail.getRedTeam().toBuilder()
                .players(enhancedRedPlayers)
                .build();

        return gameDetail.toBuilder()
                .blueTeam(enhancedBlueTeam)
                .redTeam(enhancedRedTeam)
                .build();
    }

    private List<GameDetailDto.PlayerDetailDto> enhancePlayersWithRank(
            List<GameDetailDto.PlayerDetailDto> players,
            List<MatchDetailDto.ParticipantDto> participants
    ) {
        return players.stream()
                .map(player -> {
                    // 해당 플레이어의 원본 참가자 정보 찾기
                    MatchDetailDto.ParticipantDto participant = participants.stream()
                            .filter(p -> p.getPuuid().equals(player.getPuuid()))
                            .findFirst()
                            .orElse(null);

                    if (participant != null) {
                        // 랭크 정보 조회
                        RankInfo rankInfo = getRankInfoSafely(participant.getPuuid());

                        // 랭크 정보로 플레이어 정보 업데이트
                        return gameDetailMapperService.mapToPlayerDetailWithRank(participant, rankInfo);
                    }

                    return player; // 원본 참가자 정보를 찾을 수 없으면 기존 정보 유지
                })
                .collect(Collectors.toList());
    }

    private RankInfo getRankInfoSafely(String puuid) {
        try {
            return riotApiService.getRankInfo(puuid);
        } catch (Exception e) {
            log.warn("랭크 정보 조회 실패 (PUUID: {}): {}", puuid, e.getMessage());
            return RankInfo.builder()
                    .tier("UNRANKED")
                    .rank("")
                    .leaguePoints(0)
                    .queueType("")
                    .fullRankString("언랭크")
                    .build();
        }
    }
}
