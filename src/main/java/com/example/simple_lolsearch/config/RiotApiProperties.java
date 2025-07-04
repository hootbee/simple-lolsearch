package com.example.simple_lolsearch.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "riot.api")
public class RiotApiProperties {
    private String baseUrl;
    private String apiKey;
    private String region;
    private String platform;
}

