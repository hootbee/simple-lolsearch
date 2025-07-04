package com.example.simple_lolsearch.config;

import com.example.simple_lolsearch.service.SummonerService;
import com.example.simple_lolsearch.service.impl.SummonerServiceImpl;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(RiotApiProperties.class)
public class AppConfig {

    @Bean
    public WebClient riotAsiaWebClient(RiotApiProperties properties) {
        return WebClient.builder()
                .baseUrl("https://asia.api.riotgames.com")
                .defaultHeader("X-Riot-Token", properties.getApiKey())
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
                .build();
    }

    @Bean
    public SummonerService summonerService(WebClient riotAsiaWebClient) {
        return new SummonerServiceImpl(riotAsiaWebClient);
    }
}
