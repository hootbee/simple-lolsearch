package com.example.simple_lolsearch.controller;

import com.example.simple_lolsearch.dto.*;
import com.example.simple_lolsearch.service.SummonerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/summoner")
@RequiredArgsConstructor
public class SummonerController {

    private final SummonerService summonerService;

    @GetMapping("/account")
    public ResponseEntity<AccountDto> getAccount(
            @RequestParam String gameName,
            @RequestParam String tagLine) {

        log.info("계정 조회 요청: {}#{}", gameName, tagLine);

        try {
            AccountDto account = summonerService.getAccountByRiotId(gameName, tagLine);
            return ResponseEntity.ok(account);
        } catch (Exception e) {
            log.error("계정 조회 실패: {}#{}", gameName, tagLine, e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/matches")
    public ResponseEntity<List<String>> getRecentMatches(
            @RequestParam String puuid,
            @RequestParam(defaultValue = "10") int count) {

        log.info("최근 매치 조회 요청: {}, count: {}", puuid, count);

        try {
            List<String> matchIds = summonerService.getRecentMatchIds(puuid, count);
            return ResponseEntity.ok(matchIds);
        } catch (Exception e) {
            log.error("매치 조회 실패: {}", puuid, e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/match/{matchId}")
    public ResponseEntity<MatchDetailDto> getMatchDetail(@PathVariable String matchId) {

        log.info("매치 상세 조회 요청: {}", matchId);

        try {
            MatchDetailDto matchDetail = summonerService.getMatchDetail(matchId);
            return ResponseEntity.ok(matchDetail);
        } catch (Exception e) {
            log.error("매치 상세 조회 실패: {}", matchId, e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/game-history")
    public ResponseEntity<List<GameSummaryDto>> getGameHistory(
            @RequestParam String gameName,
            @RequestParam String tagLine,
            @RequestParam(defaultValue = "10") int count) {

        log.info("게임 기록 조회 요청: {}#{}, count: {}", gameName, tagLine, count);

        try {
            // 1. 계정 정보 조회
            AccountDto account = summonerService.getAccountByRiotId(gameName, tagLine);
            String puuid = account.getPuuid();

            // 2. 최근 매치 ID 조회
            List<String> matchIds = summonerService.getRecentMatchIds(puuid, count);

            // 3. 각 매치의 상세 정보를 게임 요약으로 변환
            List<GameSummaryDto> gameHistory = matchIds.stream()
                    .map(matchId -> {
                        MatchDetailDto matchDetail = summonerService.getMatchDetail(matchId);
                        return summonerService.convertToGameSummary(matchDetail, puuid);
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(gameHistory);

        } catch (Exception e) {
            log.error("게임 기록 조회 실패: {}#{}", gameName, tagLine, e);
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/league")
    public ResponseEntity<List<LeagueEntryDto>> getLeagueEntries(@RequestParam String puuid) {

        log.info("리그 정보 조회 요청: {}", puuid);

        try {
            List<LeagueEntryDto> leagueEntries = summonerService.getLeagueEntriesByPuuid(puuid);
            return ResponseEntity.ok(leagueEntries);
        } catch (Exception e) {
            log.error("리그 정보 조회 실패: {}", puuid, e);
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/profile")
    public ResponseEntity<PlayerProfileDto> getPlayerProfile(
            @RequestParam String gameName,
            @RequestParam String tagLine) {

        log.info("플레이어 프로필 조회 요청: {}#{}", gameName, tagLine);

        try {
            // 1. 계정 정보 조회
            AccountDto account = summonerService.getAccountByRiotId(gameName, tagLine);
            String puuid = account.getPuuid();

            // 2. 리그 정보 조회
            List<LeagueEntryDto> leagueEntries = summonerService.getLeagueEntriesByPuuid(puuid);

            // 3. 통합 프로필 생성
            PlayerProfileDto profile = PlayerProfileDto.builder()
                    .account(account)
                    .leagueEntries(leagueEntries)
                    .build();

            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            log.error("플레이어 프로필 조회 실패: {}#{}", gameName, tagLine, e);
            return ResponseEntity.badRequest().build();
        }
    }
}
