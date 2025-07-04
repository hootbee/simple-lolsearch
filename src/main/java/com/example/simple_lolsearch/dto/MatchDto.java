package com.example.simple_lolsearch.dto;

import lombok.Data;

import java.util.List;

@Data
public class MatchDto {
    private MetadataDto metadata;
    private InfoDto info;

    @Data
    public static class MetadataDto{
        private String dataVersion;
        private String matchId;
        private List<String> participants;
    }

    @Data
    public static class InfoDto {
        private long gameCreatrion;
        private long gameDuration;
        private String gameMode;
        private String gameType;
        private List<ParticipantDto> participants;
    }
    @Data
    public static class ParticipantDto {
        private String puuid;
        private String summonerName;
        private String championName;
        private int championId;
        private int kills;
        private int deaths;
        private int assists;
        private boolean win;
        private int item0;
        private int item1;
        private int item2;
        private int item3;
        private int item4;
        private int item5;
        private int item6;
    }
}
