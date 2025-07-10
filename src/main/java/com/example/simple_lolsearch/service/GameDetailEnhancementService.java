package com.example.simple_lolsearch.service;

import com.example.simple_lolsearch.dto.GameDetailDto;
import com.example.simple_lolsearch.dto.MatchDetailDto;

import java.util.List;

public interface GameDetailEnhancementService {
    GameDetailDto enhanceWithRankInfo(GameDetailDto gameDetail, MatchDetailDto matchDetail);
    List<GameDetailDto.PlayerDetailDto> enhancePlayersWithRank(
            List<GameDetailDto.PlayerDetailDto> players,
            List<MatchDetailDto.ParticipantDto> participants
    );
}
