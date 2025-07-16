package com.example.simple_lolsearch.dto.common;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class ItemSpellInfo {
    private List<Integer> items;
    private int trinket;
    private int summonerSpell1Id;
    private int summonerSpell2Id;
}