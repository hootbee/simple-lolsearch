package com.example.simple_lolsearch.service;

import com.example.simple_lolsearch.dto.*;
import java.util.List;

public interface RiotApiService {
    AccountDto getAccount(String gameName, String tagLine);
    List<String> getMatchIds(String puuid, int count);
    MatchDetailDto getMatchDetail(String matchId);
    List<LeagueEntryDto> getLeagueEntries(String puuid);
    PlayerProfileDto getSummoner(String puuid);

    LeagueEntryDto getRankInfo(String puuid);
    LeagueEntryDto getRankInfoSafely(String puuid);
    AccountDto getAccountByPuuid(String puuid);

}
