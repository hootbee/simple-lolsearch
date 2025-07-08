package com.example.simple_lolsearch.service.impl;

import com.example.simple_lolsearch.dto.*;
import com.example.simple_lolsearch.service.RiotApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RiotApiServiceImpl implements RiotApiService {

    private final WebClient riotAsiaWebClient;
    private final WebClient riotKrWebClient;

    @Override
    public AccountDto getAccount(String gameName, String tagLine) {
        log.debug("Riot ID로 계정 조회: {}#{}", gameName, tagLine);

        try {
            return riotAsiaWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/riot/account/v1/accounts/by-riot-id/{gameName}/{tagLine}")
                            .build(gameName, tagLine))
                    .retrieve()
                    .bodyToMono(AccountDto.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            log.warn("플레이어를 찾을 수 없습니다: {}#{}", gameName, tagLine);
            throw new RuntimeException("플레이어를 찾을 수 없습니다: " + gameName + "#" + tagLine);
        } catch (WebClientResponseException.Forbidden e) {
            log.error("API 키 권한 오류: {}#{}", gameName, tagLine);
            throw new RuntimeException("API 접근 권한이 없습니다");
        } catch (Exception e) {
            log.error("계정 조회 중 예상치 못한 오류: {}#{}", gameName, tagLine, e);
            throw new RuntimeException("계정 조회 중 오류가 발생했습니다", e);
        }
    }

    @Override
    public List<String> getMatchIds(String puuid, int count) {
        log.debug("PUUID로 최근 매치 ID 조회: {}, count: {}", puuid, count);

        try {
            return riotAsiaWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/lol/match/v5/matches/by-puuid/{puuid}/ids")
                            .queryParam("start", 0)
                            .queryParam("count", count)
                            .build(puuid))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                    .block();
        } catch (Exception e) {
            log.error("매치 ID 조회 실패: {}", e.getMessage());
            throw new RuntimeException("매치 ID를 조회할 수 없습니다: " + puuid, e);
        }
    }

    @Override
    public MatchDetailDto getMatchDetail(String matchId) {
        log.debug("매치 상세 정보 조회: {}", matchId);

        try {
            return riotAsiaWebClient.get()
                    .uri("/lol/match/v5/matches/{matchId}", matchId)
                    .retrieve()
                    .bodyToMono(MatchDetailDto.class)
                    .block();
        } catch (Exception e) {
            log.error("매치 상세 정보 조회 실패: {}", e.getMessage());
            throw new RuntimeException("매치 상세 정보를 조회할 수 없습니다: " + matchId, e);
        }
    }

    @Override
    public List<LeagueEntryDto> getLeagueEntries(String puuid) {
        log.debug("PUUID로 리그 정보 조회: {}", puuid);

        try {
            return riotKrWebClient.get()
                    .uri("/lol/league/v4/entries/by-puuid/{puuid}", puuid)
                    .retrieve()
                    .bodyToFlux(LeagueEntryDto.class)
                    .collectList()
                    .block();
        } catch (Exception e) {
            log.error("리그 정보 조회 실패: {}", e.getMessage());
            throw new RuntimeException("리그 정보를 조회할 수 없습니다: " + puuid, e);
        }
    }

    @Override
    public SummonerDto getSummoner(String puuid) {
        log.debug("PUUID로 소환사 정보 조회: {}", puuid);

        try {
            return riotKrWebClient.get()
                    .uri("/lol/summoner/v4/summoners/by-puuid/{puuid}", puuid)
                    .retrieve()
                    .bodyToMono(SummonerDto.class)
                    .block();
        } catch (Exception e) {
            log.error("소환사 정보 조회 실패: {}", e.getMessage());
            throw new RuntimeException("소환사 정보를 조회할 수 없습니다: " + puuid, e);
        }
    }
}
