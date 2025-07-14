package com.example.simple_lolsearch.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class LeagueEntryDto {
    private String leagueId;
    private String queueType;
    private String tier;
    private String rank;
    private String summonerId;
    private String puuid;
    private int leaguePoints;
    private int wins;
    private int losses;
    private boolean veteran;
    private boolean inactive;
    private boolean freshBlood;
    private boolean hotStreak;
    private String fullRankString;

    public static LeagueEntryDto createUnrankedInfo() {
        return LeagueEntryDto.builder()
                .tier("UNRANKED")
                .rank("")
                .leaguePoints(0)
                .queueType("")
                .wins(0)
                .losses(0)
                .veteran(false)
                .inactive(false)
                .freshBlood(false)
                .hotStreak(false)
                .fullRankString("언랭크")
                .build();
    }
    public static String translateTierToKorean(String tier) {
        switch (tier.toUpperCase()) {
            case "IRON": return "아이언";
            case "BRONZE": return "브론즈";
            case "SILVER": return "실버";
            case "GOLD": return "골드";
            case "PLATINUM": return "플래티넘";
            case "EMERALD": return "에메랄드";
            case "DIAMOND": return "다이아몬드";
            case "MASTER": return "마스터";
            case "GRANDMASTER": return "그랜드마스터";
            case "CHALLENGER": return "챌린저";
            default: return "언랭크";
        }
    }
    public String generateFullRankString() {
        if ("UNRANKED".equals(tier) || tier == null) {
            return "언랭크";
        }

        String tierKorean = translateTierToKorean(tier);

        if (rank == null || rank.isEmpty()) {
            return String.format("%s %dLP", tierKorean, leaguePoints);
        }

        return String.format("%s %s %dLP", tierKorean, rank, leaguePoints);
    }
    public static String formatRankString(LeagueEntryDto leagueEntry) {
        String tierKorean = translateTierToKorean(leagueEntry.getTier());
        String rankRoman = leagueEntry.getRank();
        int lp = leagueEntry.getLeaguePoints();

        return String.format("%s %s %dLP", tierKorean, rankRoman, lp);
    }

}
