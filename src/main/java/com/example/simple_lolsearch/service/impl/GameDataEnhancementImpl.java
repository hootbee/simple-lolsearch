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
public class GameDataEnhancementImpl implements GameDataEnhancement {
    private final PlayerDataService playerDataService;
    private final GameDataMapper gameDataMapper;

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
                    .playerInfo(player.getPlayerInfo().toBuilder()
                            .tier(rankInfo.getTier())
                            .rank(rankInfo.getRank())
                            .leaguePoints(rankInfo.getLeaguePoints())
                            .build())
                    .build();
        }

        // 랭크 정보가 없는 경우 기본값 유지
        return player.toBuilder()
                .playerInfo(player.getPlayerInfo().toBuilder()
                        .tier("UNRANKED")
                        .rank("")
                        .leaguePoints(0)
                        .build())
                .build();
    }

    /**
     * 킬관여율 계산을 포함한 향상된 플레이어 정보 생성
     */

    /**
     * 팀 통계 정보를 포함한 플레이어 정보 향상
     */


    /**
     * 랭크 정보와 팀 통계 정보를 모두 포함한 완전한 향상
     */
//    @Override
//    public GameDetailDto enhanceWithAllInfo(GameDetailDto gameDetail, MatchDetailDto matchDetail) {
//        // 1. 랭크 정보 향상
//        GameDetailDto rankEnhanced = enhanceWithRankInfo(gameDetail, matchDetail);
//
//        // 2. 팀 통계 정보 향상
//        GameDetailDto fullyEnhanced = enhanceWithTeamStats(rankEnhanced, matchDetail);
//
//        return fullyEnhanced;
//    }
}