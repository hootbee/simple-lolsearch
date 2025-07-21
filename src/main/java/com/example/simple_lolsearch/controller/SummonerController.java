package com.example.simple_lolsearch.controller;

import com.example.simple_lolsearch.dto.*;
import com.example.simple_lolsearch.dto.match.GameDetailDto;
import com.example.simple_lolsearch.dto.match.GameSummaryDto;
import com.example.simple_lolsearch.service.MatchDataService;
import com.example.simple_lolsearch.service.PlayerDataService;
import com.example.simple_lolsearch.service.SummonerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/summoner")
@Slf4j
public class SummonerController {

    private final SummonerService summonerService;
    private final PlayerDataService playerDataService;
    private final MatchDataService matchDataService;

    /* 1. 계정 정보 */
    @GetMapping("/account")
    public ResponseEntity<AccountDto> getAccount(
            @RequestParam String gameName,
            @RequestParam String tagLine) {

        log.info("계정 조회 요청: {}#{}", gameName, tagLine);
        return wrap(() -> summonerService.getAccountByRiotId(gameName, tagLine));
    }

    /* 2. 최근 매치 ID */
    @GetMapping("/matches")
    public ResponseEntity<List<String>> getRecentMatches(
            @RequestParam String puuid,
            @RequestParam(defaultValue = "10") int count) {

        log.info("최근 매치 조회 요청: {}, count={}", puuid, count);
        return wrap(() -> summonerService.getRecentMatchIds(puuid, count));
    }


// 초기 로드 (최신 게임들) - 기존 방식 유지
@GetMapping("/game-history")
public ResponseEntity<List<GameSummaryDto>> getGameHistory(
        @RequestParam String gameName,
        @RequestParam String tagLine,
        @RequestParam(defaultValue = "10") int count) {

    log.info("게임 기록 조회 요청: {}#{}, count={}", gameName, tagLine, count);

    return wrap(() -> {
        // 1. gameName, tagLine으로 PUUID 조회
        String puuid = summonerService
                .getAccountByRiotId(gameName, tagLine)
                .getPuuid();

        // 2. 시간 기반 서비스 호출 (lastGameTime = null이면 최신 게임들)
        return matchDataService.getGameHistory(puuid, null, count);
    });
}

    // 🔥 새로 추가: PUUID 기반 게임 히스토리 조회
//    @GetMapping("/game-history/by-puuid")
//    public ResponseEntity<List<GameSummaryDto>> getGameHistoryByPuuid(
//            @RequestParam String puuid,
//            @RequestParam(defaultValue = "10") int count) {
//
//        log.info("PUUID 기반 게임 기록 조회 요청: puuid={}, count={}", puuid, count);
//        return wrap(() -> matchDetailService.getGameHistory(puuid, null, count));
//    }



    // 더보기 (시간 기반)
    @GetMapping("/game-history/load-more")
    public ResponseEntity<List<GameSummaryDto>> loadMoreGameHistory(
            @RequestParam String puuid,
            @RequestParam Long lastGameTime,
            @RequestParam(defaultValue = "5") int count) {

        return wrap(() -> matchDataService.getGameHistory(puuid, lastGameTime, count));
    }



    @GetMapping("/game-detail/{matchId}")
    public ResponseEntity<GameDetailDto> getGameDetail(@PathVariable String matchId) {
        log.info("게임 상세 분석 조회 요청: {}", matchId);
        return wrap(() -> matchDataService.getGameDetail(matchId));
    }


    /* 5. 프로필 조회(캐싱) */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<PlayerProfileDto>> getProfile(
            @RequestParam String gameName,
            @RequestParam String tagLine) {

        log.info("플레이어 프로필 조회 요청: {}#{}", gameName, tagLine);
        return wrapApi(() -> playerDataService.getPlayerProfile(gameName, tagLine));
    }

    /* 6. 프로필 강제 갱신 */
    @PostMapping("/profile/refresh")
    public ResponseEntity<ApiResponse<PlayerProfileDto>> refreshProfile(
            @RequestParam String gameName,
            @RequestParam String tagLine) {

        log.info("플레이어 프로필 강제 갱신 요청: {}#{}", gameName, tagLine);
        return wrapApi(() -> playerDataService.refreshPlayerProfile(gameName, tagLine));
    }

    /* 7. 리그 정보 */
    @GetMapping("/league")
    public ResponseEntity<List<LeagueEntryDto>> getLeagues(@RequestParam String puuid) {
        log.info("리그 정보 조회 요청: {}", puuid);
        return wrap(() -> summonerService.getLeagueEntriesByPuuid(puuid));
    }

    /* ---------- 공통 래퍼 ---------- */

    private <T> ResponseEntity<T> wrap(ServiceCall<T> call) {
        try {
            return ResponseEntity.ok(call.exec());
        } catch (RuntimeException e) {
            log.error("요청 처리 실패", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("서버 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private <T> ResponseEntity<ApiResponse<T>> wrapApi(ServiceCall<T> call) {
        try {
            return ResponseEntity.ok(ApiResponse.success(call.exec()));
        } catch (RuntimeException e) {
            log.error("요청 처리 실패", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("서버 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("서버 오류가 발생했습니다"));
        }
    }/* 8. PUUID로 계정 정보 조회 */
    @GetMapping("/account/by-puuid")
    public ResponseEntity<AccountDto> getAccountByPuuid(
            @RequestParam String puuid) {

        log.info("PUUID로 계정 조회 요청: {}", puuid);
        return wrap(() -> summonerService.getAccountByPuuid(puuid));
    }



    @FunctionalInterface
    private interface ServiceCall<T> {
        T exec() throws Exception;
    }
}
