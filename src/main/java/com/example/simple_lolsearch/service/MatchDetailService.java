package com.example.simple_lolsearch.service;

import com.example.simple_lolsearch.dto.match.GameDetailDto;
import com.example.simple_lolsearch.dto.match.GameSummaryDto;

import java.util.List;

public interface MatchDetailService {
    GameSummaryDto getGameSummary(String matchId, String puuid);

    GameDetailDto getGameDetail(String matchId);

    GameDetailDto refreshGameDetail(String matchId);

    List<GameSummaryDto> getGameSummaries(List<String> matchIds, String puuid);

    List<GameSummaryDto> getGameHistoryWithPagination(String puuid, int start, int count);

    List<GameSummaryDto> getGameHistory(String puuid, Long lastGameTime, int count);
}
