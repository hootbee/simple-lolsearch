package com.example.simple_lolsearch.service;

import com.example.simple_lolsearch.dto.*;
import com.example.simple_lolsearch.dto.match.MatchDetailDto;

import java.util.List;

public interface SummonerService {
    AccountDto getAccountByRiotId(String gameName, String tagLine);

    List<String> getRecentMatchIds(String puuid,int count);

    List<String> getRecentMatchIds(String puuid, int start, int count);

    MatchDetailDto getMatchDetail(String matchId);



    List<LeagueEntryDto> getLeagueEntriesByPuuid(String puuid);
    PlayerProfileDto getSummonerByPuuid(String puuid);

    AccountDto getAccountByPuuid(String puuid);
//    GameDetailDto getGameDetail(String matchId);

}
