package com.example.simple_lolsearch.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class GameDetailDto {
    // 게임 기본 정보
    private String matchId;
    private long gameDuration;
    private String gameMode;
    private String gameType;
    private long gameCreation;
    private String gameDate;
    private String relativeTime;
    private int mapId;

    // 팀별 정보
    private TeamDetailDto blueTeam;  // 100팀
    private TeamDetailDto redTeam;   // 200팀

    // 게임 통계
    private GameStatsDto gameStats;

    @Data
    @Builder(toBuilder = true)
    public static class TeamDetailDto {
        private int teamId;
        private boolean win;
        private List<PlayerDetailDto> players;
        private TeamStatsDto teamStats;
        private List<MatchDetailDto.BanDto> bans;
        private MatchDetailDto.ObjectivesDto objectives;
    }

    @Data
    @Builder(toBuilder = true)
    public static class PlayerDetailDto {
        private String puuid;
        private String riotIdGameName;
        private String riotIdTagline;
        private String championName;
        private int championId;

        // 기본 스탯
        private int kills;
        private int deaths;
        private int assists;
        private String kda;
        private double killParticipation; // 킬관여율

        // 게임 스탯
        private int cs; // totalMinionsKilled + neutralMinionsKilled
        private int goldEarned;
        private int totalDamageDealtToChampions;
        private int totalDamageTaken; // 받은 피해량
        private int visionScore;

        // 아이템 & 스펠
        private List<Integer> items;
        private int trinket;
        private int summonerSpell1Id;
        private int summonerSpell2Id;

        // 룬 정보
        private int keystoneId;
        private int primaryRuneTree;
        private int secondaryRuneTree;
        private List<Integer> runes;
        private List<Integer> statRunes;

        // 포지션 정보
        private String lane;
        private String role;

        // 랭크 정보 (별도 API 호출 필요)
        private String tier;
        private String rank;
        private int leaguePoints;
    }

    @Data
    @Builder
    public static class TeamStatsDto {
        private int totalKills;
        private int totalDeaths;
        private int totalAssists;
        private int totalGold;
        private int totalDamage;

        // 오브젝트 정보
        private int baronKills;
        private int dragonKills;
        private int riftHeraldKills;
        private int towerKills;
        private int inhibitorKills;

        // 선취점 정보
        private boolean firstBlood;
    }

    @Data
    @Builder
    public static class GameStatsDto {
        // 전체 게임 통계
        private int totalKills;
        private int totalDeaths;
        private int totalAssists;
    }
}
