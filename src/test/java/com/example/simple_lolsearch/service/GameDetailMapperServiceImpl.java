package com.example.simple_lolsearch.service;

import com.example.simple_lolsearch.dto.GameDetailDto;
import com.example.simple_lolsearch.dto.MatchDetailDto;
import com.example.simple_lolsearch.service.impl.GameDetailMapperServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat; // 수정된 import

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("GameDetailMapperService 실제 데이터 통합 테스트")
class GameDetailMapperServiceRealDataTest {

    @Autowired
    private GameDetailMapperServiceImpl gameDetailMapperService;

    @Autowired
    private RiotApiService riotApiService;

    private static final String REAL_MATCH_ID = "KR_7682656030";

    @Test
    @DisplayName("실제 매치 데이터로 게임 상세 정보 매핑 테스트")
    void mapToGameDetail_WithRealData() {
        // Given - 실제 매치 데이터 조회
        MatchDetailDto realMatchData = riotApiService.getMatchDetail(REAL_MATCH_ID);

        // When - 게임 상세 정보 매핑
        GameDetailDto result = gameDetailMapperService.mapToGameDetail(realMatchData);

        // Then - 기본 정보 검증
        assertThat(result).isNotNull();
        assertThat(result.getMatchId()).isEqualTo(REAL_MATCH_ID);
        assertThat(result.getGameDuration()).isGreaterThan(0);
        assertThat(result.getGameMode()).isNotEmpty();
        assertThat(result.getMapId()).isGreaterThan(0);

        // 팀 정보 검증
        assertThat(result.getBlueTeam()).isNotNull();
        assertThat(result.getRedTeam()).isNotNull();
        assertThat(result.getBlueTeam().getTeamId()).isEqualTo(100);
        assertThat(result.getRedTeam().getTeamId()).isEqualTo(200);

        // 각 팀에 5명씩 플레이어가 있는지 확인 - 수정됨
        assertThat(result.getBlueTeam().getPlayers()).hasSize(5);
        assertThat(result.getRedTeam().getPlayers()).hasSize(5);

        // 게임 통계 검증
        assertThat(result.getGameStats()).isNotNull();
        assertThat(result.getGameStats().getTotalKills()).isGreaterThan(0);

        System.out.println("=== 실제 매치 데이터 테스트 결과 ===");
        System.out.println("매치 ID: " + result.getMatchId());
        System.out.println("게임 모드: " + result.getGameMode());
        System.out.println("게임 시간: " + result.getGameDuration() + "초");
        System.out.println("블루팀 승리: " + result.getBlueTeam().isWin());
        System.out.println("레드팀 승리: " + result.getRedTeam().isWin());
    }

    @Test
    @DisplayName("실제 데이터로 플레이어 상세 정보 검증")
    void validatePlayerDetails_WithRealData() {
        // Given
        MatchDetailDto realMatchData = riotApiService.getMatchDetail(REAL_MATCH_ID);
        GameDetailDto result = gameDetailMapperService.mapToGameDetail(realMatchData);

        // When - 첫 번째 플레이어 선택
        GameDetailDto.PlayerDetailDto firstPlayer = result.getBlueTeam().getPlayers().get(0);

        // Then - 플레이어 정보 검증
        assertThat(firstPlayer.getPuuid()).isNotEmpty();

        // 소환사 이름 검증 - 수정됨 (안전한 검증)
        assertThat(firstPlayer.getSummonerName()).isNotNull();

        assertThat(firstPlayer.getChampionName()).isNotEmpty();
        assertThat(firstPlayer.getChampionId()).isGreaterThan(0);

        // KDA 정보 검증
        assertThat(firstPlayer.getKills()).isGreaterThanOrEqualTo(0);
        assertThat(firstPlayer.getDeaths()).isGreaterThanOrEqualTo(0);
        assertThat(firstPlayer.getAssists()).isGreaterThanOrEqualTo(0);
        assertThat(firstPlayer.getKda()).isNotEmpty();

        // 게임 스탯 검증
        assertThat(firstPlayer.getCs()).isGreaterThanOrEqualTo(0);
        assertThat(firstPlayer.getGoldEarned()).isGreaterThan(0);
        assertThat(firstPlayer.getVisionScore()).isGreaterThanOrEqualTo(0);

        // 아이템 정보 검증 - 수정됨
        assertThat(firstPlayer.getItems()).hasSize(6);
        assertThat(firstPlayer.getTrinket()).isGreaterThanOrEqualTo(0);

        // 스펠 정보 검증
        assertThat(firstPlayer.getSummonerSpell1Id()).isGreaterThan(0);
        assertThat(firstPlayer.getSummonerSpell2Id()).isGreaterThan(0);

        // 룬 정보 검증
        assertThat(firstPlayer.getKeystoneId()).isGreaterThan(0);
        assertThat(firstPlayer.getPrimaryRuneTree()).isGreaterThan(0);
        assertThat(firstPlayer.getSecondaryRuneTree()).isGreaterThan(0);

        System.out.println("=== 첫 번째 플레이어 정보 ===");
        System.out.println("소환사명: " + firstPlayer.getSummonerName());
        System.out.println("챔피언: " + firstPlayer.getChampionName());
        System.out.println("KDA: " + firstPlayer.getKills() + "/" + firstPlayer.getDeaths() + "/" + firstPlayer.getAssists());
        System.out.println("KDA 비율: " + firstPlayer.getKda());
        System.out.println("CS: " + firstPlayer.getCs());
        System.out.println("골드: " + firstPlayer.getGoldEarned());
        System.out.println("랭크: " + firstPlayer.getTier() + " " + firstPlayer.getRank());
    }

    // 나머지 테스트 메서드들은 동일...
}
