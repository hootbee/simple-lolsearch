package com.example.simple_lolsearch.service;

import com.example.simple_lolsearch.dto.GameSummaryDto;
import com.example.simple_lolsearch.dto.MatchDetailDto;

public interface GameDataMapperService {
    GameSummaryDto mapToGameSummary(MatchDetailDto match, String puuid);
    String calculateKDA(int kills, int deaths, int assists);
}
