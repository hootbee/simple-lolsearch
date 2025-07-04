package com.example.simple_lolsearch.service;

import com.example.simple_lolsearch.dto.AccountDto;

public interface SummonerService {
    AccountDto getAccountByRiotId(String gameName, String tagLine);

}
