package com.example.simple_lolsearch.controller;


import com.example.simple_lolsearch.dto.AccountDto;
import com.example.simple_lolsearch.dto.GameSummaryDto;
import com.example.simple_lolsearch.dto.MatchDetailDto;
import com.example.simple_lolsearch.service.SummonerService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SummonerController.class)
@DisplayName("SummonerController 테스트")
class SummonerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SummonerService summonerService;

    @Test
    @DisplayName("계정 조회 성공 테스트")
    void getAccount_Success() throws Exception {
        // given
        String gameName = "TestPlayer";
        String tagLine = "KR1";

        AccountDto mockAccount = new AccountDto();
        mockAccount.setPuuid("test-puuid-123");
        mockAccount.setGameName(gameName);
        mockAccount.setTagLine(tagLine);

        when(summonerService.getAccountByRiotId(gameName, tagLine))
                .thenReturn(mockAccount);

        // when & then
        mockMvc.perform(get("/api/summoner/account")
                        .param("gameName", gameName)
                        .param("tagLine", tagLine)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.puuid").value("test-puuid-123"))
                .andExpect(jsonPath("$.gameName").value(gameName))
                .andExpect(jsonPath("$.tagLine").value(tagLine));
    }

    @Test
    @DisplayName("계정 조회 실패 테스트 - 존재하지 않는 소환사")
    void getAccount_NotFound() throws Exception {
        // given
        String gameName = "NonExistentPlayer";
        String tagLine = "KR1";

        when(summonerService.getAccountByRiotId(gameName, tagLine))
                .thenThrow(WebClientResponseException.NotFound.class);

        // when & then
        mockMvc.perform(get("/api/summoner/account")
                        .param("gameName", gameName)
                        .param("tagLine", tagLine)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("최근 매치 조회 성공 테스트")
    void getRecentMatches_Success() throws Exception {
        // given
        String puuid = "test-puuid-123";
        int count = 5;

        List<String> mockMatchIds = Arrays.asList(
                "KR_7703480987",
                "KR_7703454005",
                "KR_7703410951",
                "KR_7702985058",
                "KR_7702956102"
        );

        when(summonerService.getRecentMatchIds(puuid, count))
                .thenReturn(mockMatchIds);

        // when & then
        mockMvc.perform(get("/api/summoner/matches")
                        .param("puuid", puuid)
                        .param("count", String.valueOf(count))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0]").value("KR_7703480987"))
                .andExpect(jsonPath("$[4]").value("KR_7702956102"));
    }

    @Test
    @DisplayName("매치 상세 조회 성공 테스트")
    void getMatchDetail_Success() throws Exception {
        // given
        String matchId = "KR_7703480987";

        MatchDetailDto mockMatchDetail = new MatchDetailDto();
        MatchDetailDto.MetadataDto metadata = new MatchDetailDto.MetadataDto();
        metadata.setMatchId(matchId);
        mockMatchDetail.setMetadata(metadata);

        MatchDetailDto.InfoDto info = new MatchDetailDto.InfoDto();
        info.setGameMode("CLASSIC");
        info.setGameDuration(1800L);
        mockMatchDetail.setInfo(info);

        when(summonerService.getMatchDetail(matchId))
                .thenReturn(mockMatchDetail);

        // when & then
        mockMvc.perform(get("/api/summoner/match/{matchId}", matchId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metadata.matchId").value(matchId))
                .andExpect(jsonPath("$.info.gameMode").value("CLASSIC"))
                .andExpect(jsonPath("$.info.gameDuration").value(1800));
    }

    @Test
    @DisplayName("게임 기록 조회 성공 테스트")
    void getGameHistory_Success() throws Exception {
        // given
        String gameName = "TestPlayer";
        String tagLine = "KR1";
        int count = 3;

        // Mock 계정 정보
        AccountDto mockAccount = new AccountDto();
        mockAccount.setPuuid("test-puuid-123");
        mockAccount.setGameName(gameName);
        mockAccount.setTagLine(tagLine);

        // Mock 매치 ID 목록
        List<String> mockMatchIds = Arrays.asList(
                "KR_7703480987",
                "KR_7703454005",
                "KR_7703410951"
        );

        // Mock 매치 상세 정보
        MatchDetailDto mockMatchDetail = new MatchDetailDto();
        MatchDetailDto.MetadataDto metadata = new MatchDetailDto.MetadataDto();
        metadata.setMatchId("KR_7703480987");
        mockMatchDetail.setMetadata(metadata);

        // Mock 게임 요약 정보
        GameSummaryDto mockGameSummary = GameSummaryDto.builder()
                .matchId("KR_7703480987")
                .championName("Jinx")
                .kills(10)
                .deaths(3)
                .assists(8)
                .win(true)
                .kda("6.00")
                .cs(180)
                .build();

        when(summonerService.getAccountByRiotId(gameName, tagLine))
                .thenReturn(mockAccount);
        when(summonerService.getRecentMatchIds("test-puuid-123", count))
                .thenReturn(mockMatchIds);
        when(summonerService.getMatchDetail(anyString()))
                .thenReturn(mockMatchDetail);
        when(summonerService.convertToGameSummary(any(MatchDetailDto.class), eq("test-puuid-123")))
                .thenReturn(mockGameSummary);

        // when & then
        mockMvc.perform(get("/api/summoner/game-history")
                        .param("gameName", gameName)
                        .param("tagLine", tagLine)
                        .param("count", String.valueOf(count))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].championName").value("Jinx"))
                .andExpect(jsonPath("$[0].kills").value(10))
                .andExpect(jsonPath("$[0].win").value(true));
    }

    @Test
    @DisplayName("필수 파라미터 누락 테스트")
    void getAccount_MissingParameters() throws Exception {
        // when & then
        mockMvc.perform(get("/api/summoner/account")
                        .param("gameName", "TestPlayer")
                        // tagLine 파라미터 누락
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

}
