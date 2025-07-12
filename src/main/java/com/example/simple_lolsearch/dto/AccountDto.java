package com.example.simple_lolsearch.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class AccountDto {
    private String puuid;
    private String gameName;
    private String tagLine;

}
