package com.example.simple_lolsearch.dto;

import lombok.Data;

@Data
public class SummonerDto {
    private String id;
    private String accountId;
    private String puuId;
    private String name;
    private int profileIconId;
    private long revisionDate;
    private int summonerLevel;
}
