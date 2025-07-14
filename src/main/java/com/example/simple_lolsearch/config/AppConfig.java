package com.example.simple_lolsearch.config;

import com.example.simple_lolsearch.service.*;
import com.example.simple_lolsearch.service.impl.*;
import com.example.simple_lolsearch.util.RuneExtractorUtil;
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
                .baseUrl("https://asia.api.riotgames.com")
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
                .baseUrl("https://kr.api.riotgames.com")
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
    public TimeFormatterService timeFormatterService() {
        return new TimeFormatterServiceImpl();
    }

    @Bean
    public RiotApiService riotApiService(
            @Qualifier("riotAsiaWebClient") WebClient riotAsiaWebClient,
            @Qualifier("riotKrWebClient") WebClient riotKrWebClient) {
        return new RiotApiServiceImpl(riotAsiaWebClient, riotKrWebClient);
    }
    @Bean
    public GameDetailMapperService gameDetailMapperService(
            TimeFormatterService timeFormatterService, RuneExtractorUtil runeExtractorUtil
            ) {
        return new GameDetailMapperServiceImpl(timeFormatterService,runeExtractorUtil);
    }

    @Bean
    public GameDetailEnhancementService gameDetailEnhancementService(
            PlayerDataService playerDataService,
            GameDetailMapperService gameDetailMapperService) {
        return new GameDetailEnhancementServiceImpl(playerDataService, gameDetailMapperService);
    }
    // ✅ 수정된 코드: Spring이 관리하는 빈을 주입받음
    @Bean
    public SummonerService summonerService(
            RiotApiService riotApiService) { // 파라미터로 주입받음
        return new SummonerServiceImpl(riotApiService);
    }
}
