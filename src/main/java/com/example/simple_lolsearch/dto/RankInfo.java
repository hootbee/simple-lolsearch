package com.example.simple_lolsearch.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RankInfo {
    private String tier;          // "GOLD", "PLATINUM" 등
    private String rank;          // "I", "II", "III", "IV"
    private int leaguePoints;     // LP
    private String queueType;     // "RANKED_SOLO_5x5", "RANKED_FLEX_SR"
    private String fullRankString; // "골드 II 45LP"
}
