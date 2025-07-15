//package com.example.simple_lolsearch.service;
//
//import com.example.simple_lolsearch.dto.*;
//import com.example.simple_lolsearch.service.impl.SummonerServiceImpl;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@ActiveProfiles("local")
//@SpringBootTest
//public class RiotSummonerServiceTest {
//    @Autowired
//    private SummonerService summonerService;
//
//    @Test
//    void getAccountByRiotId_T1Faker_shouldReturnPuuid() {
//        // given
//        String gameName = "땡야땡야땡야땡야";
//        String tagLine = "KR1";
//
//        System.out.println("=== 요청 정보 ===");
//        System.out.println("GameName: " + gameName);
//        System.out.println("TagLine: " + tagLine);
//
//        // when
//        AccountDto result = summonerService.getAccountByRiotId(gameName, tagLine);
//
//        // then
//        assertNotNull(result);
//        assertNotNull(result.getPuuid());
//
//        System.out.println("=== 실제 반환된 데이터 ===");
//        System.out.println("PUUID: " + result.getPuuid());
//        System.out.println("GameName: '" + result.getGameName() + "'");
//        System.out.println("TagLine: '" + result.getTagLine() + "'");
//
//        // 실제 데이터에 맞게 검증
//        assertTrue(result.getGameName().contains("땡야땡야땡야땡야"));
//    }
//
//    @Test
//    void getMatchDetail_shouldReturnDetailedMatchInfo() {
//        // given
//        String gameName = "숨쉬머";
//        String tagLine = "KR1";
//
//        System.out.println("=== 매치 상세 정보 조회 테스트 ===");
//
//        // when
//        AccountDto account = summonerService.getAccountByRiotId(gameName, tagLine);
//        String puuid = account.getPuuid();
//
//        List<String> matchIds = summonerService.getRecentMatchIds(puuid, 1);
//        assertFalse(matchIds.isEmpty(), "매치 기록이 없습니다");
//
//        String firstMatchId = matchIds.get(0);
//        MatchDetailDto matchDetail = summonerService.getMatchDetail(firstMatchId);
//
//        // then
//        assertNotNull(matchDetail);
//        assertNotNull(matchDetail.getMetadata());
//        assertNotNull(matchDetail.getInfo());
//        assertEquals(firstMatchId, matchDetail.getMetadata().getMatchId());
//        assertEquals(10, matchDetail.getInfo().getParticipants().size());
//
//        System.out.println("=== 매치 상세 정보 ===");
//        System.out.println("Match ID: " + matchDetail.getMetadata().getMatchId());
//        System.out.println("게임 모드: " + matchDetail.getInfo().getGameMode());
//        System.out.println("게임 시간: " + matchDetail.getInfo().getGameDuration() + "초");
//        System.out.println("맵: " + matchDetail.getInfo().getMapId());
//
//        // 해당 플레이어 정보 찾기
//        MatchDetailDto.ParticipantDto playerInfo = matchDetail.getInfo().getParticipants().stream()
//                .filter(p -> p.getPuuid().equals(puuid))
//                .findFirst()
//                .orElse(null);
//
//        assertNotNull(playerInfo, "플레이어 정보를 찾을 수 없습니다");
//
//        System.out.println("=== 플레이어 정보 ===");
//        System.out.println("챔피언: " + playerInfo.getChampionName());
//        System.out.println("KDA: " + playerInfo.getKills() + "/" + playerInfo.getDeaths() + "/" + playerInfo.getAssists());
//        System.out.println("승리: " + (playerInfo.isWin() ? "승리" : "패배"));
//        System.out.println("CS: " + (playerInfo.getTotalMinionsKilled() + playerInfo.getNeutralMinionsKilled()));
//        System.out.println("골드: " + playerInfo.getGoldEarned());
//        System.out.println("시야 점수: " + playerInfo.getVisionScore());
//        System.out.println("라인: " + playerInfo.getLane());
//        System.out.println("역할: " + playerInfo.getRole());
//    }
//
//    @Test
//    void convertToGameSummary_shouldCreateSummary() {
//        // given
//        String gameName = "숨쉬머";
//        String tagLine = "KR1";
//
//        // when
//        AccountDto account = summonerService.getAccountByRiotId(gameName, tagLine);
//        String puuid = account.getPuuid();
//
//        List<String> matchIds = summonerService.getRecentMatchIds(puuid, 1);
//        String firstMatchId = matchIds.get(0);
//        MatchDetailDto matchDetail = summonerService.getMatchDetail(firstMatchId);
//
//        GameSummaryDto summary = ((SummonerServiceImpl) summonerService)
//                .convertToGameSummary(matchDetail, puuid);
//
//        // then
//        assertNotNull(summary);
//        assertEquals(firstMatchId, summary.getMatchId());
//        assertNotNull(summary.getChampionName());
//        assertTrue(summary.getKills() >= 0);
//        assertTrue(summary.getDeaths() >= 0);
//        assertTrue(summary.getAssists() >= 0);
//
//        System.out.println("=== 게임 요약 정보 ===");
//        System.out.println("챔피언: " + summary.getChampionName());
//        System.out.println("KDA: " + summary.getKills() + "/" + summary.getDeaths() + "/" + summary.getAssists() + " (" + summary.getKda() + ")");
//        System.out.println("결과: " + (summary.isWin() ? "승리" : "패배"));
//        System.out.println("CS: " + summary.getCs());
//        System.out.println("골드: " + summary.getGoldEarned());
//    }
//    @Test
//    @DisplayName("PUUID로 소환사 정보 조회 테스트")
//    void getSummonerByPuuid_Success() {
//        // given
//        String puuid = "sR6S9_2yZIic24-oSLv1mQ9wx8oAbL7Qhz441d6_tgm9oGLh_7h2KOjBZn1EszNDrXo1zceX4bFZdw";
//
//        System.out.println("=== PUUID로 소환사 정보 조회 테스트 ===");
//
//        // when
//        SummonerDto summoner = summonerService.getSummonerByPuuid(puuid);
//
//        // then
//        assertNotNull(summoner);
//        assertNotNull(summoner.getId());
//        assertEquals(puuid, summoner.getPuuid());
//        assertTrue(summoner.getProfileIconId() > 0);
//        assertTrue(summoner.getSummonerLevel() > 0);
//        assertTrue(summoner.getRevisionDate() > 0);
//
//        System.out.println("=== 소환사 정보 검증 완료 ===");
//        System.out.println("Summoner ID: " + summoner.getId());
//        System.out.println("Profile Icon ID: " + summoner.getProfileIconId());
//        System.out.println("Level: " + summoner.getSummonerLevel());
//        System.out.println("Profile Icon URL: http://ddragon.leagueoflegends.com/cdn/14.24.1/img/profileicon/" + summoner.getProfileIconId() + ".png");
//    }
//
//}
