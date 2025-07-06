package com.example.simple_lolsearch.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SummonerDto {
    private String id;
    private String puuid;
    private int profileIconId;
    private long revisionDate;
    private int summonerLevel;
}