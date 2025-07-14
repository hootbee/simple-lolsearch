package com.example.simple_lolsearch.service.impl;
import com.example.simple_lolsearch.dto.GameDetailDto;
import com.example.simple_lolsearch.dto.LeagueEntryDto;
import com.example.simple_lolsearch.dto.MatchDetailDto;
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

    public List<GameDetailDto.PlayerDetailDto> enhancePlayersWithRank(
            List<GameDetailDto.PlayerDetailDto> players,
            List<MatchDetailDto.ParticipantDto> participants
    ) {
        return players.stream()
                .map(player -> {
                    MatchDetailDto.ParticipantDto participant = participants.stream()
                            .filter(p -> p.getPuuid().equals(player.getPuuid()))
                            .findFirst()
                            .orElse(null);

                    if (participant != null) {
                        // 🔥 DB 우선 조회로 변경
                        LeagueEntryDto rankInfo = playerDataService.getRankInfoFromDbOrApi(participant.getPuuid());
                        return gameDetailMapperService.mapToPlayerDetailWithRank(participant, rankInfo);
                    }

                    return player;
                })
                .collect(Collectors.toList());
    }
}
