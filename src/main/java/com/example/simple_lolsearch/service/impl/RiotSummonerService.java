package com.example.simple_lolsearch.service.impl;
import com.example.simple_lolsearch.config.RiotApiProperties;
import com.example.simple_lolsearch.dto.SummonerDto;
import com.example.simple_lolsearch.service.SummonerService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class RiotSummonerService implements SummonerService {

    private final WebClient webClient;
    private final RiotApiProperties riotApiProperties;

    public RiotSummonerService(WebClient webClient, RiotApiProperties riotApiProperties) {
        this.webClient = webClient;
        this.riotApiProperties = riotApiProperties;
    }

    @Override
    public SummonerDto getSummonerByName(String summonerName) {
        return webClient.get()
                .uri(riotApiProperties.getBaseUrl() + "/lol/summoner/v4/summoners/by-name/{summonerName}", summonerName)
                .header("X-Riot-Token", riotApiProperties.getApiKey())
                .retrieve()
                .bodyToMono(SummonerDto.class)
                .block();
    }
}
