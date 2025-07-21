package com.example.simple_lolsearch.dto.match;

import com.example.simple_lolsearch.dto.common.*;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class GameSummaryDto {
    private BaseGameInfo gameInfo;
    private BasePlayerInfo playerInfo;
    private GameStats gameStats;
    private ItemSpellInfo itemSpellInfo;
    private RuneInfo runeInfo;

    // 추가 정보
    private String kda;
    private int cs;
    private String gameDate;
    private String relativeTime;
    private String detailedTime;

    // === 편의 메서드들 (하위 호환성) ===

    // 게임 정보 편의 메서드
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

    // 플레이어 정보 편의 메서드
    public String getChampionName() {
        return playerInfo != null ? playerInfo.getChampionName() : null;
    }

    public int getChampionId() {
        return playerInfo != null ? playerInfo.getChampionId() : 0;
    }

    public String getLane() {
        return playerInfo != null ? playerInfo.getLane() : null;
    }

    public String getRole() {
        return playerInfo != null ? playerInfo.getRole() : null;
    }

    public String getPuuid() {
        return playerInfo != null ? playerInfo.getPuuid() : null;
    }

    public String getRiotIdGameName() {
        return playerInfo != null ? playerInfo.getRiotIdGameName() : null;
    }

    // 게임 스탯 편의 메서드
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
    public Integer getTotalDamageDealtToChampions() {
        return gameStats != null ? gameStats.getTotalDamageDealtToChampions() : 0;
    }
    public Integer getTotalDamageTaken() {
        return gameStats != null ? gameStats.getTotalDamageTaken() : 0;
    }
    public Integer getLargestMultiKill() {
        return gameStats != null ? gameStats.getLargestMultiKill() : 0;
    }

    public int getVisionScore() {
        return gameStats != null ? gameStats.getVisionScore() : 0;
    }

    // 아이템 & 스펠 편의 메서드
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

    // 룬 편의 메서드
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
}

