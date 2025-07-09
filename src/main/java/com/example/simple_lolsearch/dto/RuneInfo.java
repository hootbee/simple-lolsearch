package com.example.simple_lolsearch.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RuneInfo {
    private int keystoneId;
    private int primaryRuneTree;
    private int secondaryRuneTree;
    private List<Integer> runes;
    private List<Integer> statRunes;

}
