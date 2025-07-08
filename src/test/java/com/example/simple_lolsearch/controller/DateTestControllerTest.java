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
@DisplayName("날짜 포맷팅 테스트")
public class DateTestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("날짜 포맷팅 API 테스트")
    void testDateFormat() throws Exception {
        mockMvc.perform(get("/api/test/date-format")
                        .param("gameName", "숨쉬머")
                        .param("tagLine", "KR1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerName").value("숨쉬머#KR1"))
                .andExpect(jsonPath("$.currentTime").exists())
                .andExpect(jsonPath("$.games").isArray())
                .andExpect(jsonPath("$.games[0].formattedDate").exists())
                .andDo(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    System.out.println("=== 날짜 포맷팅 테스트 결과 ===");
                    System.out.println(responseBody);
                });
    }
}
