package com.example.simple_lolsearch.service.impl;

import com.example.simple_lolsearch.dto.match.GameDetailDto;
import com.example.simple_lolsearch.dto.LeagueEntryDto;
import com.example.simple_lolsearch.dto.match.MatchDetailDto;
import com.example.simple_lolsearch.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameDetailEnhancementServiceImpl implements GameDetailEnhancementService {
    private final PlayerDataService playerDataService;
    private final GameDetailMapperService gameDetailMapperService;

    @Override
    public GameDetailDto enhanceWithRankInfo(GameDetailDto gameDetail, MatchDetailDto matchDetail) {
        // 1. 모든 플레이어의 PUUID 수집
        List<String> allPuuids = new ArrayList<>();
        gameDetail.getBlueTeam().getPlayers().forEach(p -> allPuuids.add(p.getPlayerInfo().getPuuid()));
        gameDetail.getRedTeam().getPlayers().forEach(p -> allPuuids.add(p.getPlayerInfo().getPuuid()));

        // 2. 한 번에 모든 플레이어의 랭크 정보 조회
        Map<String, LeagueEntryDto> rankInfoMap = playerDataService.getRankInfoByPuuids(allPuuids);

        // 3. 블루팀 플레이어 랭크 정보 업데이트
        List<GameDetailDto.PlayerDetailDto> enhancedBluePlayers =
                gameDetail.getBlueTeam().getPlayers().stream()
                        .map(player -> {
                            String puuid = player.getPlayerInfo().getPuuid();
                            LeagueEntryDto rankInfo = rankInfoMap.get(puuid);
                            return updatePlayerWithRank(player, rankInfo);
                        })
                        .collect(Collectors.toList());

        // 4. 레드팀 플레이어 랭크 정보 업데이트
        List<GameDetailDto.PlayerDetailDto> enhancedRedPlayers =
                gameDetail.getRedTeam().getPlayers().stream()
                        .map(player -> {
                            String puuid = player.getPlayerInfo().getPuuid();
                            LeagueEntryDto rankInfo = rankInfoMap.get(puuid);
                            return updatePlayerWithRank(player, rankInfo);
                        })
                        .collect(Collectors.toList());

        // 5. 업데이트된 플레이어 정보로 팀 정보 재구성
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


    private GameDetailDto.PlayerDetailDto updatePlayerWithRank(
            GameDetailDto.PlayerDetailDto player,
            LeagueEntryDto rankInfo) {

        if (rankInfo != null) {
            return player.toBuilder()
                    .tier(rankInfo.getTier())
                    .rank(rankInfo.getRank())
                    .leaguePoints(rankInfo.getLeaguePoints())
                    .build();
        }

        // 랭크 정보가 없는 경우 기본값 유지
        return player.toBuilder()
                .tier("UNRANKED")
                .rank("")
                .leaguePoints(0)
                .build();
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
