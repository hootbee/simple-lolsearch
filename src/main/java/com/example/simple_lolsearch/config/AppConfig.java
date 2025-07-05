package com.example.simple_lolsearch.config;

    import com.example.simple_lolsearch.service.SummonerService;
    import com.example.simple_lolsearch.service.impl.SummonerServiceImpl;
    import org.springframework.beans.factory.annotation.Qualifier;
    import org.springframework.boot.context.properties.EnableConfigurationProperties;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.web.reactive.function.client.WebClient;

    @Configuration
    @EnableConfigurationProperties(RiotApiProperties.class)
    public class AppConfig {

        @Bean
        @Qualifier("riotAsiaWebClient")
        public WebClient riotAsiaWebClient(RiotApiProperties properties) {
            return WebClient.builder()
                    .baseUrl("https://asia.api.riotgames.com")  // Account API용
                    .defaultHeader("X-Riot-Token", properties.getApiKey())
                    .build();
        }

        @Bean
        @Qualifier("riotKrWebClient")
        public WebClient riotKrWebClient(RiotApiProperties properties) {
            return WebClient.builder()
                    .baseUrl("https://kr.api.riotgames.com")   // League API용
                    .defaultHeader("X-Riot-Token", properties.getApiKey())
                    .build();
        }

        @Bean
        public SummonerService summonerService(
                @Qualifier("riotAsiaWebClient") WebClient riotAsiaWebClient,
                @Qualifier("riotKrWebClient") WebClient riotKrWebClient) {
            return new SummonerServiceImpl(riotAsiaWebClient, riotKrWebClient);
        }
    }