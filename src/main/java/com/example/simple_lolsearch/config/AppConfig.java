package com.example.simple_lolsearch.config;

import com.example.simple_lolsearch.service.GameDataMapperService;
import com.example.simple_lolsearch.service.RiotApiService;
import com.example.simple_lolsearch.service.SummonerService;
import com.example.simple_lolsearch.service.TimeFormatterService;
import com.example.simple_lolsearch.service.impl.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import io.netty.channel.ChannelOption;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties(RiotApiProperties.class)
public class AppConfig {

    @Bean
    @Qualifier("riotAsiaWebClient")
    public WebClient riotAsiaWebClient(RiotApiProperties properties) {
        return WebClient.builder()
                .baseUrl("https://asia.api.riotgames.com")  // Account API용
                .defaultHeader("X-Riot-Token", properties.getApiKey())
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .responseTimeout(Duration.ofSeconds(30))
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                ))
                .build();
    }

    @Bean
    @Qualifier("riotKrWebClient")
    public WebClient riotKrWebClient(RiotApiProperties properties) {
        return WebClient.builder()
                .baseUrl("https://kr.api.riotgames.com")   // League API용
                .defaultHeader("X-Riot-Token", properties.getApiKey())
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .responseTimeout(Duration.ofSeconds(30))
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                ))
                .build();
    }

    // 시간 포맷팅 서비스
    @Bean
    public TimeFormatterService timeFormatterService() {
        return new TimeFormatterServiceImpl();
    }

    // Riot API 호출 서비스
    @Bean
    public RiotApiService riotApiService(
            @Qualifier("riotAsiaWebClient") WebClient riotAsiaWebClient,
            @Qualifier("riotKrWebClient") WebClient riotKrWebClient) {
        return new RiotApiServiceImpl(riotAsiaWebClient, riotKrWebClient);
    }

    // 게임 데이터 매핑 서비스
    @Bean
    public GameDataMapperService gameDataMapperService(TimeFormatterService timeFormatterService) {
        return new GameDataMapperServiceImpl(timeFormatterService);
    }

    // 메인 소환사 서비스 (리팩토링된 구조)
    @Bean
    public SummonerService summonerService(
            RiotApiService riotApiService,
            GameDataMapperService gameDataMapperService) {
        return new SummonerServiceImpl(riotApiService, gameDataMapperService);
    }
}
