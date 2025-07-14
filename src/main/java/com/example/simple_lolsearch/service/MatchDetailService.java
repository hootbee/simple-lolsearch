package com.example.simple_lolsearch.service;

import com.example.simple_lolsearch.dto.GameDetailDto;
import com.example.simple_lolsearch.dto.GameSummaryDto;

import java.util.List;

public interface MatchDetailService {
    GameSummaryDto getGameSummary(String matchId, String puuid);

    GameDetailDto getGameDetail(String matchId);

    GameDetailDto refreshGameDetail(String matchId);

    List<GameSummaryDto> getGameSummaries(List<String> matchIds, String puuid);
}
