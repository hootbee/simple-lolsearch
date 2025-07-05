package com.example.simple_lolsearch.service.impl;

import com.example.simple_lolsearch.dto.AccountDto;
import com.example.simple_lolsearch.dto.LeagueEntryDto;
import com.example.simple_lolsearch.service.SummonerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@AllArgsConstructor
public class SummonerServiceImpl implements SummonerService {
    private final WebClient riotAsiaWebClient;
    private final WebClient riotKrWebClient;

    @Override
    public AccountDto getAccountByRiotId(String gameName, String tagLine) {
        log.debug("Riot ID로 계정 조회: {}#{}", gameName, tagLine);

        // UriBuilder 사용으로 자동 인코딩 (수동 인코딩 제거)
        return riotAsiaWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/riot/account/v1/accounts/by-riot-id/{gameName}/{tagLine}")
                        .build(gameName, tagLine))  // Spring이 자동으로 올바르게 인코딩
                .retrieve()
                .bodyToMono(AccountDto.class)
                .block();
    }

    @Override
    public String getPuuidByRiotId(String gameName, String tagLine) {
        log.debug("Riot ID로 PUUID 조회: {}#{}", gameName, tagLine);

        AccountDto account = getAccountByRiotId(gameName, tagLine);

        System.out.println("=== PUUID 추출 결과 ===");
        System.out.println("PUUID: " + account.getPuuid());

        return account.getPuuid();
    }
    @Override
    public List<LeagueEntryDto> getLeagueEntriesByPuuid(String puuid) {
        log.debug("PUUID로 리그 정보 직접 조회: {}", puuid);

        System.out.println("=== 리그 정보 조회 (PUUID 직접) ===");
        System.out.println("PUUID: " + puuid);
        System.out.println("요청 URL: https://kr.api.riotgames.com/lol/league/v4/entries/by-puuid/" + puuid);

        try {
            List<LeagueEntryDto> leagueEntries = riotKrWebClient.get()
                    .uri("/lol/league/v4/entries/by-puuid/{puuid}", puuid)
                    .retrieve()
                    .bodyToFlux(LeagueEntryDto.class)
                    .collectList()
                    .block();

            // 결과 출력 코드 (완성)
            System.out.println("=== 리그 정보 결과 ===");
            System.out.println("총 " + leagueEntries.size() + "개의 리그 정보 조회됨");

            for (LeagueEntryDto entry : leagueEntries) {
                System.out.println("Queue: " + entry.getQueueType() +
                        ", Tier: " + entry.getTier() + " " + entry.getRank() +
                        ", LP: " + entry.getLeaguePoints() +
                        ", W/L: " + entry.getWins() + "/" + entry.getLosses());
            }

            return leagueEntries;

        } catch (Exception e) {
            log.error("리그 정보 조회 실패: {}", e.getMessage());
            throw new RuntimeException("리그 정보를 조회할 수 없습니다: " + puuid, e);
        }
    }



}
