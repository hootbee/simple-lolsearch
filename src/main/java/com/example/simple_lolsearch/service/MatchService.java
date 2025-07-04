package com.example.simple_lolsearch.service;

import com.example.simple_lolsearch.dto.GameSummaryDto;

import java.util.List;

public interface MatchService {
    List<String> getRecentMatchIds(String puuid, int count);

    List<GameSummaryDto> getGameSummaries(String puuid, List<String> matchIds);
}
