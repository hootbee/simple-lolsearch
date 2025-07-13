package com.example.simple_lolsearch.service;

import com.example.simple_lolsearch.dto.RankInfo;
import com.example.simple_lolsearch.entity.PlayerEntity;

// PlayerService.java
public interface PlayerService {
    RankInfo getRankInfoFromDbOrApi(String puuid);
    PlayerEntity saveOrUpdatePlayer(String puuid, RankInfo rankInfo);
    boolean isPlayerDataStale(PlayerEntity player);
}
