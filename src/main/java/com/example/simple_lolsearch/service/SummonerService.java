package com.example.simple_lolsearch.service;

import com.example.simple_lolsearch.dto.AccountDto;
import com.example.simple_lolsearch.dto.GameSummaryDto;
import com.example.simple_lolsearch.dto.LeagueEntryDto;
import com.example.simple_lolsearch.dto.MatchDetailDto;

import java.util.List;

public interface SummonerService {
    AccountDto getAccountByRiotId(String gameName, String tagLine);

    List<String> getRecentMatchIds(String puuid,int count);

    MatchDetailDto getMatchDetail(String matchId);

    GameSummaryDto convertToGameSummary(MatchDetailDto match, String puuid);
}
