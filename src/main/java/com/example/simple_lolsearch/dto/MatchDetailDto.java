package com.example.simple_lolsearch.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchDetailDto {
    private MetadataDto metadata;
    private InfoDto info;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MetadataDto {
        private String dataVersion;
        private String matchId;
        private List<String> participants;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InfoDto {
        private long gameCreation;
        private long gameDuration;
        private String gameMode;
        private String gameType;
        private String gameVersion;
        private int mapId;
        private List<ParticipantDto> participants;
        private List<TeamDto> teams;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ParticipantDto {
        private String puuid;
        private String summonerName;
        private String championName;
        private int championId;
        private int kills;
        private int deaths;
        private int assists;
        private boolean win;
        private int totalMinionsKilled;
        private int neutralMinionsKilled;
        private int goldEarned;
        private int totalDamageDealtToChampions;
        private int visionScore;
        private int item0;
        private int item1;
        private int item2;
        private int item3;
        private int item4;
        private int item5;
        private int item6;
        private String lane;
        private String role;
        private int teamId;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamDto {
        private int teamId;
        private boolean win;
        private List<BanDto> bans;
        private ObjectivesDto objectives;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BanDto {
        private int championId;
        private int pickTurn;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ObjectivesDto {
        private ObjectiveDto baron;
        private ObjectiveDto champion;
        private ObjectiveDto dragon;
        private ObjectiveDto inhibitor;
        private ObjectiveDto riftHerald;
        private ObjectiveDto tower;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ObjectiveDto {
        private boolean first;
        private int kills;
    }
}
