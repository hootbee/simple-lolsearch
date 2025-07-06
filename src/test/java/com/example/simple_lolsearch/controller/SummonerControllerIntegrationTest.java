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
@DisplayName("SummonerController 통합 테스트")
class SummonerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Test
    void checkApiKeyConfiguration() {
        // given
        System.out.println("=== API 키 설정 확인 ===");

        // RiotApiProperties 빈 주입해서 확인
        // 또는 직접 properties 파일 값 확인
    }


    @Test
    @DisplayName("실제 API 호출 통합 테스트")
    void realApiCall_IntegrationTest() throws Exception {
        // given
        String gameName = "숨쉬머";
        String tagLine = "KR1";

        System.out.println("=== 테스트 요청 정보 ===");
        System.out.println("Test Request URL: /api/summoner/account?gameName=" + gameName + "&tagLine=" + tagLine);

        // when & then
        mockMvc.perform(get("/api/summoner/account")
                        .param("gameName", gameName)
                        .param("tagLine", tagLine)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())  // 이것이 MockMvc 요청/응답 정보를 출력
                .andDo(result -> {
                    // 추가 정보 출력
                    System.out.println("=== MockMvc 요청 결과 ===");
                    System.out.println("Status: " + result.getResponse().getStatus());
                    System.out.println("Content: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.puuid").exists())
                .andExpect(jsonPath("$.gameName").exists())
                .andExpect(jsonPath("$.tagLine").value(tagLine));
    }

    @Test
    @DisplayName("헬스 체크 테스트")
    void healthCheck() throws Exception {
        mockMvc.perform(get("/api/test/health"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }
}
