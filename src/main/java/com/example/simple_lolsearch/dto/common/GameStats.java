package com.example.simple_lolsearch.dto.common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class GameStats {
    private int kills;
    private int deaths;
    private int assists;
    private int goldEarned;
    private int champLevel;
    private Integer totalDamageDealtToChampions;
    private Integer totalDamageTaken;
    private Integer largestMultiKill;
    private String kda;
    private Double killParticipation;
    private Integer cs;
    private int visionScore;
    private boolean win;
}