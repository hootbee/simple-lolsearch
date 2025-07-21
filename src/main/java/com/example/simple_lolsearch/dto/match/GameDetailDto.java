package com.example.simple_lolsearch.dto.match;

import com.example.simple_lolsearch.dto.common.*;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class GameDetailDto {
    // 게임 기본 정보
    private BaseGameInfo gameInfo;
    private String gameDate;
    private String relativeTime;

    private TeamDetailDto blueTeam;
    private TeamDetailDto redTeam;
    private GameStatsDto gameStats;

    // === 편의 메서드들 ===
    public String getMatchId() {
        return gameInfo != null ? gameInfo.getMatchId() : null;
    }

    public long getGameDuration() {
        return gameInfo != null ? gameInfo.getGameDuration() : 0;
    }

    public String getGameMode() {
        return gameInfo != null ? gameInfo.getGameMode() : null;
    }

    public long getGameCreation() {
        return gameInfo != null ? gameInfo.getGameCreation() : 0;
    }

    public Integer getQueueId() {
        return gameInfo != null ? gameInfo.getQueueId() : null;
    }

    public int getMapId() {
        return gameInfo != null ? gameInfo.getMapId() : 0;
    }

    public String getGameType() {
        return gameInfo != null ? gameInfo.getGameType() : null;
    }

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
        private BasePlayerInfo playerInfo;
        private GameStats gameStats;
        private ItemSpellInfo itemSpellInfo;
        private RuneInfo runeInfo;

        // === 편의 메서드들 ===
        public String getPuuid() {
            return playerInfo != null ? playerInfo.getPuuid() : null;
        }

        public String getChampionName() {
            return playerInfo != null ? playerInfo.getChampionName() : null;
        }

        public int getChampionId() {
            return playerInfo != null ? playerInfo.getChampionId() : 0;
        }

        public String getRiotIdGameName() {
            return playerInfo != null ? playerInfo.getRiotIdGameName() : null;
        }

        public String getLane() {
            return playerInfo != null ? playerInfo.getLane() : null;
        }

        public String getRole() {
            return playerInfo != null ? playerInfo.getRole() : null;
        }

        public int getKills() {
            return gameStats != null ? gameStats.getKills() : 0;
        }

        public int getDeaths() {
            return gameStats != null ? gameStats.getDeaths() : 0;
        }

        public int getAssists() {
            return gameStats != null ? gameStats.getAssists() : 0;
        }

        public boolean isWin() {
            return gameStats != null ? gameStats.isWin() : false;
        }

        public int getGoldEarned() {
            return gameStats != null ? gameStats.getGoldEarned() : 0;
        }

        public int getChampLevel() {
            return gameStats != null ? gameStats.getChampLevel() : 0;
        }

        public int getVisionScore() {
            return gameStats != null ? gameStats.getVisionScore() : 0;
        }

        public List<Integer> getItems() {
            return itemSpellInfo != null ? itemSpellInfo.getItems() : null;
        }

        public int getTrinket() {
            return itemSpellInfo != null ? itemSpellInfo.getTrinket() : 0;
        }

        public int getSummonerSpell1Id() {
            return itemSpellInfo != null ? itemSpellInfo.getSummonerSpell1Id() : 0;
        }

        public int getSummonerSpell2Id() {
            return itemSpellInfo != null ? itemSpellInfo.getSummonerSpell2Id() : 0;
        }

        public int getKeystoneId() {
            return runeInfo != null ? runeInfo.getKeystoneId() : 0;
        }

        public int getPrimaryRuneTree() {
            return runeInfo != null ? runeInfo.getPrimaryRuneTree() : 0;
        }

        public int getSecondaryRuneTree() {
            return runeInfo != null ? runeInfo.getSecondaryRuneTree() : 0;
        }

        public List<Integer> getRunes() {
            return runeInfo != null ? runeInfo.getRunes() : null;
        }

        public List<Integer> getStatRunes() {
            return runeInfo != null ? runeInfo.getStatRunes() : null;
        }
        public Integer getTotalDamageDealtToChampions() {
            return gameStats != null ? gameStats.getTotalDamageDealtToChampions() : 0;
        }
        public Integer getTotalDamageTaken() {
            return gameStats != null ? gameStats.getTotalDamageTaken() : 0;
        }
        public Integer getLargestMultiKill() {
            return gameStats != null ? gameStats.getLargestMultiKill() : 0;
        }
        public String getRiotIdTagline() {
            return playerInfo != null ? playerInfo.getRiotIdTagline() : null;
        }
        public String getKda() {
            return gameStats != null ? gameStats.getKda() : null;
        }
        public Double getKillParticipation() {
            return gameStats != null ? gameStats.getKillParticipation() : 0.0;
        }
        public Integer getCs() {
            return gameStats != null ? gameStats.getCs() : 0;
        }
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
