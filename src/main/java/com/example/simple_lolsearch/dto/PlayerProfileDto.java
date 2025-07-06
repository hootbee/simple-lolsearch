package com.example.simple_lolsearch.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class PlayerProfileDto {
    private AccountDto account;
    private List<LeagueEntryDto> leagueEntries;

    // 편의 메서드들
    public LeagueEntryDto getSoloRank() {
        return leagueEntries.stream()
                .filter(entry -> "RANKED_SOLO_5x5".equals(entry.getQueueType()))
                .findFirst()
                .orElse(null);
    }

    public LeagueEntryDto getFlexRank() {
        return leagueEntries.stream()
                .filter(entry -> "RANKED_FLEX_SR".equals(entry.getQueueType()))
                .findFirst()
                .orElse(null);
    }

    public boolean isUnranked() {
        return leagueEntries.isEmpty();
    }
}
