package com.example.simple_lolsearch.dto.common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class BasePlayerInfo {
    private String puuid;
    private String riotIdGameName;
    private String riotIdTagline;
    private String championName;
    private int championId;
    private String lane;
    private String role;
}