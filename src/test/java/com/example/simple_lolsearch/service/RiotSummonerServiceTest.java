package com.example.simple_lolsearch.service;

import com.example.simple_lolsearch.dto.AccountDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("local")
@SpringBootTest
public class RiotSummonerServiceTest {
    @Autowired
    private SummonerService summonerService;

    @Test
    void getAccountByRiotId_T1Faker_shouldReturnPuuid() {
        // given
        String gameName = "땡야땡야땡야땡야";
        String tagLine = "KR1";

        System.out.println("=== 요청 정보 ===");
        System.out.println("GameName: " + gameName);
        System.out.println("TagLine: " + tagLine);

        // when
        AccountDto result = summonerService.getAccountByRiotId(gameName, tagLine);

        // then
        assertNotNull(result);
        assertNotNull(result.getPuuid());

        System.out.println("=== 실제 반환된 데이터 ===");
        System.out.println("PUUID: " + result.getPuuid());
        System.out.println("GameName: '" + result.getGameName() + "'");
        System.out.println("TagLine: '" + result.getTagLine() + "'");

        // 실제 데이터에 맞게 검증
        assertTrue(result.getGameName().contains("땡야땡야땡야땡야"));
    }
    @Test
    void getPuuidByRiotId_T1Faker_shouldReturnPuuid() {
        // given
        String gameName = "땡야땡야땡야땡야";
        String tagLine = "KR1";

        System.out.println("=== PUUID 추출 테스트 ===");
        System.out.println("GameName: " + gameName);
        System.out.println("TagLine: " + tagLine);

        // when
        String puuid = summonerService.getPuuidByRiotId(gameName, tagLine);

        // then
        assertNotNull(puuid);
        assertFalse(puuid.isEmpty());
        assertTrue(puuid.length() > 50); // PUUID는 보통 78자 정도

        System.out.println("=== 추출된 PUUID ===");
        System.out.println("PUUID: " + puuid);
        System.out.println("PUUID 길이: " + puuid.length());
    }

}
