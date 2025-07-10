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
    private final GameDataMapperService gameDataMapperService;
    private final GameDetailMapperService gameDetailMapperService;
    private final GameDetailEnhancementService gameDetailEnhancementService;

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
    public GameSummaryDto convertToGameSummary(MatchDetailDto match, String puuid) {
        return gameDataMapperService.mapToGameSummary(match, puuid);
    }

    @Override
    public List<LeagueEntryDto> getLeagueEntriesByPuuid(String puuid) {
        return riotApiService.getLeagueEntries(puuid);
    }

    @Override
    public SummonerDto getSummonerByPuuid(String puuid) {
        return riotApiService.getSummoner(puuid);
    }
    @Override
    public GameDetailDto getGameDetail(String matchId) {
        log.debug("게임 상세 분석 요청: {}", matchId);

        try {
            MatchDetailDto matchDetail = riotApiService.getMatchDetail(matchId);
            GameDetailDto gameDetail = gameDetailMapperService.mapToGameDetail(matchDetail);

            // 복잡한 로직을 별도 서비스로 위임
            return gameDetailEnhancementService.enhanceWithRankInfo(gameDetail, matchDetail);

        } catch (Exception e) {
            log.error("게임 상세 분석 실패: {}", matchId, e);
            throw new RuntimeException("게임 상세 분석 중 오류가 발생했습니다: " + matchId, e);
        }
    }
}
