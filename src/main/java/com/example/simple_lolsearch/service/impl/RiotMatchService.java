package com.example.simple_lolsearch.service.impl;

import com.example.simple_lolsearch.config.RiotApiProperties;
import com.example.simple_lolsearch.dto.GameSummaryDto;
import com.example.simple_lolsearch.dto.MatchDto;
import com.example.simple_lolsearch.service.MatchService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.simple_lolsearch.dto.GameSummaryDto.*;

@Service
@AllArgsConstructor
public class RiotMatchService implements MatchService {
    private final WebClient webClient;
    private final RiotApiProperties riotApiProperties;

    @Override
    public List<String> getRecentMatchIds(String puuid, int count) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("asia.api.riotgames.com")
                        .path("/lol/match/v5/matches/by-puuid/{puuid}/ids")
                        .queryParam("start", 0)
                        .queryParam("count", count)
                        .build(puuid))
                .header("X-Riot-Token", riotApiProperties.getApiKey())
                .retrieve()
                .bodyToFlux(String.class)
                .collectList()
                .block();
    }

    @Override
    public List<GameSummaryDto> getGameSummaries(String puuid, List<String> matchIds) {
        return matchIds.stream()
                .map(matchId -> getMatchDetail(matchId))
                .map(match -> convertToGameSummary(match, puuid))
                .collect(Collectors.toList());
    }

    private MatchDto getMatchDetail(String matchId) {
        return webClient.get()
                .uri("https://asia.api.riotgames.com/lol/match/v5/matches/{matchId}", matchId)
                .header("X-Riot-Token", riotApiProperties.getApiKey())
                .retrieve()
                .bodyToMono(MatchDto.class)
                .block();
    }
    private GameSummaryDto convertToGameSummary(MatchDto match, String puuid) {
        MatchDto.ParticipantDto participant = match.getInfo().getParticipants().stream()
                .filter(p -> p.getPuuid().equals(puuid))
                .findFirst()
                .orElseThrow();

        String kda = String.format("%.2f",
                participant.getDeaths() == 0 ?
                        (double)(participant.getKills() + participant.getAssists()) :
                        (double)(participant.getKills() + participant.getAssists()) / participant.getDeaths());

        return GameSummaryDto.builder()
                .matchId(match.getMetadata().getMatchId())
                .championName(participant.getChampionName())
                .kills(participant.getKills())
                .deaths(participant.getDeaths())
                .assists(participant.getAssists())
                .win(participant.isWin())
                .gameDuration(match.getInfo().getGameDuration())
                .gameMode(match.getInfo().getGameMode())
                .kda(kda)
                .build();
    }
}
