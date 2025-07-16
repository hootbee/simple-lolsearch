package com.example.simple_lolsearch.service.impl;

import com.example.simple_lolsearch.dto.match.GameDetailDto;
import com.example.simple_lolsearch.dto.LeagueEntryDto;
import com.example.simple_lolsearch.dto.match.MatchDetailDto;
import com.example.simple_lolsearch.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameDetailEnhancementServiceImpl implements GameDetailEnhancementService {
    private final PlayerDataService playerDataService;
    private final GameDetailMapperService gameDetailMapperService;

    @Override
    public GameDetailDto enhanceWithRankInfo(GameDetailDto gameDetail, MatchDetailDto matchDetail) {
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

    @Override
    public List<GameDetailDto.PlayerDetailDto> enhancePlayersWithRank(
            List<GameDetailDto.PlayerDetailDto> players,
            List<MatchDetailDto.ParticipantDto> participants
    ) {
        return players.stream()
                .map(player -> {
                    MatchDetailDto.ParticipantDto participant = participants.stream()
                            .filter(p -> p.getPuuid().equals(player.getPlayerInfo().getPuuid()))
                            .findFirst()
                            .orElse(null);

                    if (participant != null) {
                        // DB 우선 조회로 랭크 정보 가져오기
                        LeagueEntryDto rankInfo = playerDataService.getRankInfoFromDbOrApi(participant.getPuuid());

                        // 랭크 정보가 있는 경우에만 업데이트
                        if (rankInfo != null) {
                            return player.toBuilder()
                                    .tier(rankInfo.getTier())
                                    .rank(rankInfo.getRank())
                                    .leaguePoints(rankInfo.getLeaguePoints())
                                    .build();
                        }
                    }

                    return player;
                })
                .collect(Collectors.toList());
    }

    /**
     * 킬관여율 계산을 포함한 향상된 플레이어 정보 생성
     */
    @Override
    public GameDetailDto enhanceWithTeamStats(GameDetailDto gameDetail, MatchDetailDto matchDetail) {
        // 블루팀 플레이어 킬관여율 계산
        List<GameDetailDto.PlayerDetailDto> enhancedBluePlayers = enhancePlayersWithTeamStats(
                gameDetail.getBlueTeam().getPlayers(),
                matchDetail.getInfo().getParticipants().stream()
                        .filter(p -> p.getTeamId() == 100)
                        .collect(Collectors.toList())
        );

        // 레드팀 플레이어 킬관여율 계산
        List<GameDetailDto.PlayerDetailDto> enhancedRedPlayers = enhancePlayersWithTeamStats(
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

    /**
     * 팀 통계 정보를 포함한 플레이어 정보 향상
     */
    private List<GameDetailDto.PlayerDetailDto> enhancePlayersWithTeamStats(
            List<GameDetailDto.PlayerDetailDto> players,
            List<MatchDetailDto.ParticipantDto> teamParticipants
    ) {
        // 팀 총 킬 수 계산
        int teamTotalKills = teamParticipants.stream()
                .mapToInt(MatchDetailDto.ParticipantDto::getKills)
                .sum();

        return players.stream()
                .map(player -> {
                    MatchDetailDto.ParticipantDto participant = teamParticipants.stream()
                            .filter(p -> p.getPuuid().equals(player.getPlayerInfo().getPuuid()))
                            .findFirst()
                            .orElse(null);

                    if (participant != null) {
                        // 킬관여율 계산
                        double killParticipation = calculateKillParticipation(participant, teamTotalKills);

                        return player.toBuilder()
                                .killParticipation(killParticipation)
                                .build();
                    }

                    return player;
                })
                .collect(Collectors.toList());
    }

    /**
     * 킬관여율 계산
     */
    private double calculateKillParticipation(MatchDetailDto.ParticipantDto participant, int teamTotalKills) {
        if (teamTotalKills == 0) {
            return 0.0;
        }

        double participation = (double)(participant.getKills() + participant.getAssists()) / teamTotalKills * 100;
        return Math.round(participation * 10.0) / 10.0;
    }

    /**
     * 랭크 정보와 팀 통계 정보를 모두 포함한 완전한 향상
     */
    @Override
    public GameDetailDto enhanceWithAllInfo(GameDetailDto gameDetail, MatchDetailDto matchDetail) {
        // 1. 랭크 정보 향상
        GameDetailDto rankEnhanced = enhanceWithRankInfo(gameDetail, matchDetail);

        // 2. 팀 통계 정보 향상
        GameDetailDto fullyEnhanced = enhanceWithTeamStats(rankEnhanced, matchDetail);

        return fullyEnhanced;
    }
}
