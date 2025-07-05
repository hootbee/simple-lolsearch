package com.example.simple_lolsearch.service;

import com.example.simple_lolsearch.dto.AccountDto;
import com.example.simple_lolsearch.dto.LeagueEntryDto;

import java.util.List;

public interface SummonerService {
    AccountDto getAccountByRiotId(String gameName, String tagLine);

    String getPuuidByRiotId(String gameName, String tagLine);
    List<LeagueEntryDto> getLeagueEntriesByPuuid(String puuid);  // PUUID 직접 사용
}
