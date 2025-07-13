package com.example.simple_lolsearch.service.impl;

import com.example.simple_lolsearch.dto.*;
import com.example.simple_lolsearch.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SummonerServiceImpl implements SummonerService {

    private final RiotApiService riotApiService;

    @Override
    public AccountDto getAccountByRiotId(String gameName, String tagLine) {
        return riotApiService.getAccount(gameName, tagLine);
    }

    @Override
    public List<String> getRecentMatchIds(String puuid, int count) {
        return riotApiService.getMatchIds(puuid, count);
    }

    @Override
    public MatchDetailDto getMatchDetail(String matchId) {
        return riotApiService.getMatchDetail(matchId);
    }


    @Override
    public List<LeagueEntryDto> getLeagueEntriesByPuuid(String puuid) {
        return riotApiService.getLeagueEntries(puuid);
    }

    @Override
    public PlayerProfileDto getSummonerByPuuid(String puuid) {
        return riotApiService.getSummoner(puuid);
    }

}
