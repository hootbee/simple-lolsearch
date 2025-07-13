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

    // 🔥 추가: wins, losses 필드
    @Builder.Default
    private int wins = 0;

    @Builder.Default
    private int losses = 0;

    public static RankInfo createUnrankedInfo() {
        return RankInfo.builder()
                .tier("UNRANKED")
                .rank("")
                .leaguePoints(0)
                .queueType("")
                .fullRankString("언랭크")
                .wins(0)
                .losses(0)
                .build();
    }

    public static String formatRankString(LeagueEntryDto leagueEntry) {
        String tierKorean = translateTierToKorean(leagueEntry.getTier());
        String rankRoman = leagueEntry.getRank();
        int lp = leagueEntry.getLeaguePoints();

        return String.format("%s %s %dLP", tierKorean, rankRoman, lp);
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
}
