package com.example.simple_lolsearch.service.impl;

import com.example.simple_lolsearch.dto.AccountDto;
import com.example.simple_lolsearch.service.SummonerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@AllArgsConstructor
public class SummonerServiceImpl implements SummonerService {
    private final WebClient riotAsiaWebClient;

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
}
