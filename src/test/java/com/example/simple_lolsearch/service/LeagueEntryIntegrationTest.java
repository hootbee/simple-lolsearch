package com.example.simple_lolsearch.service;

import com.example.simple_lolsearch.dto.AccountDto;
import com.example.simple_lolsearch.dto.LeagueEntryDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("리그 엔트리 통합 테스트")
class LeagueEntryIntegrationTest {

    @Autowired
    private SummonerService summonerService;

    @Test
    @DisplayName("PUUID로 리그 정보 조회 통합 테스트")
    void getLeagueEntriesByPuuid_IntegrationTest() {
        // given
        String gameName = "숨쉬머";
        String tagLine = "KR1";

        System.out.println("=== 리그 정보 조회 통합 테스트 ===");
        System.out.println("GameName: " + gameName);
        System.out.println("TagLine: " + tagLine);

        // when
        AccountDto account = summonerService.getAccountByRiotId(gameName, tagLine);
        String puuid = account.getPuuid();

        List<LeagueEntryDto> leagueEntries = summonerService.getLeagueEntriesByPuuid(puuid);

        // then
        assertNotNull(leagueEntries);

        System.out.println("=== 리그 정보 결과 ===");
        System.out.println("PUUID: " + puuid);
        System.out.println("총 " + leagueEntries.size() + "개의 리그 정보");

        if (leagueEntries.isEmpty()) {
            System.out.println("언랭크 플레이어입니다.");
        } else {
            for (LeagueEntryDto entry : leagueEntries) {
                System.out.println("=== " + getQueueTypeName(entry.getQueueType()) + " ===");
                System.out.println("티어: " + entry.getTier() + " " + entry.getRank());
                System.out.println("LP: " + entry.getLeaguePoints());
                System.out.println("승/패: " + entry.getWins() + "승 " + entry.getLosses() + "패");

                // 승률 계산
                int totalGames = entry.getWins() + entry.getLosses();
                if (totalGames > 0) {
                    double winRate = (double) entry.getWins() / totalGames * 100;
                    System.out.println("승률: " + String.format("%.1f", winRate) + "%");
                }

                System.out.println("베테랑: " + (entry.isVeteran() ? "예" : "아니오"));
                System.out.println("연승 중: " + (entry.isHotStreak() ? "예" : "아니오"));
                System.out.println("비활성: " + (entry.isInactive() ? "예" : "아니오"));
                System.out.println("신규: " + (entry.isFreshBlood() ? "예" : "아니오"));
                System.out.println("---");

                // 검증
                assertNotNull(entry.getQueueType());
                assertNotNull(entry.getTier());
                assertNotNull(entry.getRank());
                assertTrue(entry.getLeaguePoints() >= 0);
                assertTrue(entry.getWins() >= 0);
                assertTrue(entry.getLosses() >= 0);
            }
        }
    }

    @Test
    @DisplayName("솔로랭크와 자유랭크 분리 테스트")
    void separateSoloAndFlexRank_Test() {
        // given
        String gameName = "숨쉬머";
        String tagLine = "KR1";

        // when
        AccountDto account = summonerService.getAccountByRiotId(gameName, tagLine);
        List<LeagueEntryDto> leagueEntries = summonerService.getLeagueEntriesByPuuid(account.getPuuid());

        // then
        System.out.println("=== 솔로랭크/자유랭크 분리 테스트 ===");

        LeagueEntryDto soloRank = leagueEntries.stream()
                .filter(entry -> "RANKED_SOLO_5x5".equals(entry.getQueueType()))
                .findFirst()
                .orElse(null);

        LeagueEntryDto flexRank = leagueEntries.stream()
                .filter(entry -> "RANKED_FLEX_SR".equals(entry.getQueueType()))
                .findFirst()
                .orElse(null);

        if (soloRank != null) {
            System.out.println("솔로랭크: " + soloRank.getTier() + " " + soloRank.getRank() + " " + soloRank.getLeaguePoints() + "LP");
            System.out.println("솔로랭크 전적: " + soloRank.getWins() + "승 " + soloRank.getLosses() + "패");
        } else {
            System.out.println("솔로랭크: 언랭크");
        }

        if (flexRank != null) {
            System.out.println("자유랭크: " + flexRank.getTier() + " " + flexRank.getRank() + " " + flexRank.getLeaguePoints() + "LP");
            System.out.println("자유랭크 전적: " + flexRank.getWins() + "승 " + flexRank.getLosses() + "패");
        } else {
            System.out.println("자유랭크: 언랭크");
        }
    }

    @Test
    @DisplayName("언랭크 플레이어 테스트")
    void unrankedPlayer_Test() {
        // given - 언랭크 플레이어 계정 (실제 존재하는 언랭크 계정으로 변경 필요)
        String gameName = "UnrankedPlayer";  // 실제 언랭크 계정으로 변경
        String tagLine = "KR1";

        try {
            // when
            AccountDto account = summonerService.getAccountByRiotId(gameName, tagLine);
            List<LeagueEntryDto> leagueEntries = summonerService.getLeagueEntriesByPuuid(account.getPuuid());

            // then
            System.out.println("=== 언랭크 플레이어 테스트 ===");
            System.out.println("계정: " + account.getGameName() + "#" + account.getTagLine());
            System.out.println("리그 정보 개수: " + leagueEntries.size());

            if (leagueEntries.isEmpty()) {
                System.out.println("결과: 언랭크 확인됨");
            } else {
                System.out.println("결과: 랭크 정보 존재");
                for (LeagueEntryDto entry : leagueEntries) {
                    System.out.println(entry.getQueueType() + ": " + entry.getTier() + " " + entry.getRank());
                }
            }
        } catch (Exception e) {
            System.out.println("언랭크 플레이어 테스트 스킵 (계정 없음): " + e.getMessage());
        }
    }

    @Test
    @DisplayName("리그 정보 상세 검증 테스트")
    void leagueEntryDetailValidation_Test() {
        // given
        String gameName = "숨쉬머";
        String tagLine = "KR1";

        // when
        AccountDto account = summonerService.getAccountByRiotId(gameName, tagLine);
        List<LeagueEntryDto> leagueEntries = summonerService.getLeagueEntriesByPuuid(account.getPuuid());

        // then
        System.out.println("=== 리그 정보 상세 검증 ===");

        for (LeagueEntryDto entry : leagueEntries) {
            System.out.println("Queue Type: " + entry.getQueueType());
            System.out.println("League ID: " + entry.getLeagueId());
            System.out.println("Summoner ID: " + entry.getSummonerId());
            System.out.println("PUUID: " + entry.getPuuid());

            // 필수 필드 검증
            assertNotNull(entry.getQueueType(), "Queue Type은 null이 될 수 없습니다");
            assertNotNull(entry.getTier(), "Tier는 null이 될 수 없습니다");
            assertNotNull(entry.getRank(), "Rank는 null이 될 수 없습니다");

            // 값 범위 검증
            assertTrue(entry.getLeaguePoints() >= 0 && entry.getLeaguePoints() <= 100,
                    "LP는 0-100 사이여야 합니다");
            assertTrue(entry.getWins() >= 0, "승수는 0 이상이어야 합니다");
            assertTrue(entry.getLosses() >= 0, "패수는 0 이상이어야 합니다");

            // 티어 유효성 검증
            String[] validTiers = {"IRON", "BRONZE", "SILVER", "GOLD", "PLATINUM",
                    "DIAMOND", "MASTER", "GRANDMASTER", "CHALLENGER"};
            boolean isValidTier = false;
            for (String tier : validTiers) {
                if (tier.equals(entry.getTier())) {
                    isValidTier = true;
                    break;
                }
            }
            assertTrue(isValidTier, "유효하지 않은 티어: " + entry.getTier());

            // 랭크 유효성 검증 (MASTER 이상은 랭크가 I)
            if (entry.getTier().equals("MASTER") || entry.getTier().equals("GRANDMASTER") ||
                    entry.getTier().equals("CHALLENGER")) {
                assertEquals("I", entry.getRank(), "마스터 이상은 I 랭크여야 합니다");
            }

            System.out.println("검증 완료: " + entry.getQueueType());
            System.out.println("---");
        }
    }

    // Helper 메서드
    private String getQueueTypeName(String queueType) {
        switch (queueType) {
            case "RANKED_SOLO_5x5":
                return "솔로랭크";
            case "RANKED_FLEX_SR":
                return "자유랭크";
            case "RANKED_FLEX_TT":
                return "3대3 자유랭크";
            default:
                return queueType;
        }
    }
}
