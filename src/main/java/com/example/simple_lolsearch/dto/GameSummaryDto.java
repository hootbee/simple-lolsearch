package com.example.simple_lolsearch.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameSummaryDto {
    private String matchId;
    private String championName;
    private int kills;
    private int deaths;
    private int assists;
    private boolean win;
    private long gameDuration;
    private String gameMode;
    private String kda;
    private int cs;
    private int goldEarned;
    private int visionScore;
    private String lane;
    private String role;
    private String gameDate;    // 포맷된 날짜 문자열
    private long gameCreation;  // 게임 생성 시간 (밀리초)
}
