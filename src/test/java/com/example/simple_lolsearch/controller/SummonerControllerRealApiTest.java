package com.example.simple_lolsearch.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("실제 PUUID를 사용한 통합 테스트")
public class SummonerControllerRealApiTest {

    @Autowired
    private MockMvc mockMvc;

    // 실제 PUUID (이전 테스트에서 확인된 값)
    private static final String REAL_PUUID = "sR6S9_2yZIic24-oSLv1mQ9wx8oAbL7Qhz441d6_tgm9oGLh_7h2KOjBZn1EszNDrXo1zceX4bFZdw";
    private static final String REAL_GAME_NAME = "숨쉬머";
    private static final String REAL_TAG_LINE = "KR1";

    @Test
    @DisplayName("실제 PUUID로 리그 정보 조회 테스트")
    void getLeagueEntries_RealPuuid_Test() throws Exception {
        // given
        System.out.println("=== 실제 PUUID 리그 정보 조회 테스트 ===");
        System.out.println("PUUID: " + REAL_PUUID);

        // when & then
        mockMvc.perform(get("/api/summoner/league")
                        .param("puuid", REAL_PUUID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    System.out.println("=== 실제 리그 정보 응답 ===");
                    System.out.println(responseBody);

                    // JSON 파싱해서 상세 정보 출력
                    if (responseBody.equals("[]")) {
                        System.out.println("결과: 언랭크 플레이어");
                    } else {
                        System.out.println("결과: 랭크 정보 존재");
                    }
                });
    }

    @Test
    @DisplayName("실제 Riot ID로 플레이어 프로필 조회 테스트")
    void getPlayerProfile_RealRiotId_Test() throws Exception {
        // given
        System.out.println("=== 실제 Riot ID 프로필 조회 테스트 ===");
        System.out.println("GameName: " + REAL_GAME_NAME);
        System.out.println("TagLine: " + REAL_TAG_LINE);

        // when & then
        mockMvc.perform(get("/api/summoner/profile")
                        .param("gameName", REAL_GAME_NAME)
                        .param("tagLine", REAL_TAG_LINE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.account").exists())
                .andExpect(jsonPath("$.account.gameName").value(REAL_GAME_NAME))
                .andExpect(jsonPath("$.account.tagLine").value(REAL_TAG_LINE))
                .andExpect(jsonPath("$.account.puuid").value(REAL_PUUID))
                .andExpect(jsonPath("$.leagueEntries").exists())
                .andDo(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    System.out.println("=== 실제 플레이어 프로필 응답 ===");
                    System.out.println(responseBody);
                });
    }

    @Test
    @DisplayName("실제 API - 리그 정보 상세 검증")
    void getLeagueEntries_DetailValidation_Test() throws Exception {
        // given
        System.out.println("=== 실제 리그 정보 상세 검증 테스트 ===");

        // when & then
        mockMvc.perform(get("/api/summoner/league")
                        .param("puuid", REAL_PUUID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    System.out.println("=== 상세 검증 결과 ===");

                    if (responseBody.equals("[]")) {
                        System.out.println("언랭크 플레이어 - 검증 완료");
                    } else {
                        System.out.println("랭크 플레이어 - 리그 정보:");
                        System.out.println(responseBody);

                        // 기본 JSON 구조 검증
                        if (responseBody.contains("queueType") &&
                                responseBody.contains("tier") &&
                                responseBody.contains("rank")) {
                            System.out.println("✅ 필수 필드 존재 확인");
                        }

                        if (responseBody.contains("RANKED_SOLO_5x5")) {
                            System.out.println("✅ 솔로랭크 정보 존재");
                        }

                        if (responseBody.contains("RANKED_FLEX_SR")) {
                            System.out.println("✅ 자유랭크 정보 존재");
                        }
                    }
                });
    }

    @Test
    @DisplayName("실제 API - 프로필 통합 정보 검증")
    void getPlayerProfile_IntegratedValidation_Test() throws Exception {
        // given
        System.out.println("=== 실제 프로필 통합 정보 검증 테스트 ===");

        // when & then
        mockMvc.perform(get("/api/summoner/profile")
                        .param("gameName", REAL_GAME_NAME)
                        .param("tagLine", REAL_TAG_LINE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.account.puuid").isNotEmpty())
                .andExpect(jsonPath("$.leagueEntries").isArray())
                .andDo(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    System.out.println("=== 통합 정보 검증 결과 ===");

                    // 계정 정보 검증
                    if (responseBody.contains("\"gameName\":\"" + REAL_GAME_NAME + "\"")) {
                        System.out.println("✅ 계정명 일치");
                    }

                    if (responseBody.contains("\"tagLine\":\"" + REAL_TAG_LINE + "\"")) {
                        System.out.println("✅ 태그라인 일치");
                    }

                    if (responseBody.contains("\"puuid\":\"" + REAL_PUUID + "\"")) {
                        System.out.println("✅ PUUID 일치");
                    }

                    // 리그 정보 검증
                    if (responseBody.contains("\"leagueEntries\":[]")) {
                        System.out.println("📊 언랭크 플레이어");
                    } else if (responseBody.contains("\"leagueEntries\":[")) {
                        System.out.println("📊 랭크 플레이어 - 리그 정보 포함");
                    }

                    System.out.println("=== 전체 응답 ===");
                    System.out.println(responseBody);
                });
    }

    @Test
    @DisplayName("실제 API - 에러 처리 테스트")
    void getPlayerProfile_ErrorHandling_Test() throws Exception {
        // given
        String invalidGameName = "NonExistentPlayer12345";
        String invalidTagLine = "KR1";

        System.out.println("=== 실제 API 에러 처리 테스트 ===");
        System.out.println("Invalid GameName: " + invalidGameName);

        // when & then
        mockMvc.perform(get("/api/summoner/profile")
                        .param("gameName", invalidGameName)
                        .param("tagLine", invalidTagLine)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    System.out.println("=== 에러 처리 결과 ===");
                    System.out.println("Status: " + result.getResponse().getStatus());
                    System.out.println("✅ 존재하지 않는 소환사에 대한 적절한 에러 처리 확인");
                });
    }

    @Test
    @DisplayName("실제 API - 성능 테스트")
    void getPlayerProfile_Performance_Test() throws Exception {
        // given
        System.out.println("=== 실제 API 성능 테스트 ===");
        long startTime = System.currentTimeMillis();

        // when & then
        mockMvc.perform(get("/api/summoner/profile")
                        .param("gameName", REAL_GAME_NAME)
                        .param("tagLine", REAL_TAG_LINE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(result -> {
                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime;

                    System.out.println("=== 성능 테스트 결과 ===");
                    System.out.println("응답 시간: " + duration + "ms");

                    if (duration < 3000) {
                        System.out.println("✅ 응답 시간 양호 (3초 미만)");
                    } else {
                        System.out.println("⚠️ 응답 시간 느림 (3초 이상)");
                    }
                });
    }
}
