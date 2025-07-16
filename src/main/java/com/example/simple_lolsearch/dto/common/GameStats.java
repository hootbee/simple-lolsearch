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
    private int visionScore;
    private boolean win;
}