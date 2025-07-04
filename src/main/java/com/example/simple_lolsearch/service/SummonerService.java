package com.example.simple_lolsearch.service;

import com.example.simple_lolsearch.dto.SummonerDto;

public interface SummonerService {
    SummonerDto getSummonerByName(String summonerName);
}
