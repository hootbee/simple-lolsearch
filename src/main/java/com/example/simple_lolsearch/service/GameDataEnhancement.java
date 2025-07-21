package com.example.simple_lolsearch.service;

import com.example.simple_lolsearch.dto.match.GameDetailDto;
import com.example.simple_lolsearch.dto.match.MatchDetailDto;

// GameDetailEnhancementService 인터페이스에 추가 메서드 정의 필요
public interface GameDataEnhancement {
    GameDetailDto enhanceWithRankInfo(GameDetailDto gameDetail, MatchDetailDto matchDetail);
    GameDetailDto enhanceWithTeamStats(GameDetailDto gameDetail, MatchDetailDto matchDetail);
    GameDetailDto enhanceWithAllInfo(GameDetailDto gameDetail, MatchDetailDto matchDetail);
}
