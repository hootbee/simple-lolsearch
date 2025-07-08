package com.example.simple_lolsearch.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

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
    private String gameDate;
    private long gameCreation;
    private String relativeTime;
    private String detailedTime;
    private List<Integer> items;
    private int trinket;

    private int summonerSpell1Id;
    private int summonerSpell2Id;

    // 룬 정보
    private int keystoneId;
    private int primaryRuneTree;
    private int secondaryRuneTree;
    private List<Integer> runes;      // 전체 룬 ID
    private List<Integer> statRunes;  // 스탯 룬 ID
}
