package com.example.simple_lolsearch.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerProfileDto {
    private String id;
    private AccountDto account;
    private List<LeagueEntryDto> leagueEntries;
    private String summonerId;
    private int profileIconId;
    private long revisionDate;
    private int summonerLevel;
}
