package com.example.simple_lolsearch.dto.common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class BaseGameInfo {
    private String matchId;
    private long gameDuration;
    private String gameMode;
    private String gameType;
    private long gameCreation;
    private int mapId;
    private Integer queueId;
}