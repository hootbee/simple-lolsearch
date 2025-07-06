package com.example.simple_lolsearch.service.impl;

import com.example.simple_lolsearch.dto.AccountDto;
import com.example.simple_lolsearch.dto.GameSummaryDto;
import com.example.simple_lolsearch.dto.LeagueEntryDto;
import com.example.simple_lolsearch.dto.MatchDetailDto;
import com.example.simple_lolsearch.service.SummonerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@AllArgsConstructor
public class SummonerServiceImpl implements SummonerService {
    private final WebClient riotAsiaWebClient;
    private final WebClient riotKrWebClient;

    @Override
    public AccountDto getAccountByRiotId(String gameName, String tagLine) {
        log.debug("Riot ID로 계정 조회: {}#{}", gameName, tagLine);

        // UriBuilder 사용으로 자동 인코딩 (수동 인코딩 제거)
        return riotAsiaWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/riot/account/v1/accounts/by-riot-id/{gameName}/{tagLine}")
                        .build(gameName, tagLine))  // Spring이 자동으로 올바르게 인코딩
                .retrieve()
                .bodyToMono(AccountDto.class)
                .block();
    }

    @Override
    public List<String> getRecentMatchIds(String puuid, int count) {
        log.debug("PUUID로 최근 매치 ID 조회: {}, count: {}", puuid, count);

        System.out.println("=== 최근 매치 ID 조회 ===");
        System.out.println("PUUID: " + puuid);
        System.out.println("Count: " + count);
        System.out.println("요청 URL: https://asia.api.riotgames.com/lol/match/v5/matches/by-puuid/" + puuid + "/ids?start=0&count=" + count);

        try {
            // bodyToFlux 대신 bodyToMono로 List 전체를 받기
            List<String> matchIds = riotAsiaWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/lol/match/v5/matches/by-puuid/{puuid}/ids")
                            .queryParam("start", 0)
                            .queryParam("count", count)
                            .build(puuid))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<String>>() {})  // 수정된 부분
                    .block();

            System.out.println("=== 매치 ID 조회 결과 ===");
            System.out.println("총 " + matchIds.size() + "개의 매치 ID 조회됨");
            for (int i = 0; i < matchIds.size(); i++) {
                System.out.println((i + 1) + ". " + matchIds.get(i));
            }

            return matchIds;

        } catch (Exception e) {
            log.error("매치 ID 조회 실패: {}", e.getMessage());
            throw new RuntimeException("매치 ID를 조회할 수 없습니다: " + puuid, e);
        }
    }
    @Override
    public MatchDetailDto getMatchDetail(String matchId) {
        log.debug("매치 상세 정보 조회: {}", matchId);

        System.out.println("=== 매치 상세 정보 조회 ===");
        System.out.println("Match ID: " + matchId);
        System.out.println("요청 URL: https://asia.api.riotgames.com/lol/match/v5/matches/" + matchId);

        try {
            MatchDetailDto matchDetail = riotAsiaWebClient.get()
                    .uri("/lol/match/v5/matches/{matchId}", matchId)
                    .retrieve()
                    .bodyToMono(MatchDetailDto.class)
                    .block();

            System.out.println("=== 매치 상세 정보 결과 ===");
            System.out.println("게임 모드: " + matchDetail.getInfo().getGameMode());
            System.out.println("게임 시간: " + matchDetail.getInfo().getGameDuration() + "초");
            System.out.println("맵 ID: " + matchDetail.getInfo().getMapId());
            System.out.println("참가자 수: " + matchDetail.getInfo().getParticipants().size());

            return matchDetail;

        } catch (Exception e) {
            log.error("매치 상세 정보 조회 실패: {}", e.getMessage());
            throw new RuntimeException("매치 상세 정보를 조회할 수 없습니다: " + matchId, e);
        }
    }
    public GameSummaryDto convertToGameSummary(MatchDetailDto match, String puuid) {
        MatchDetailDto.ParticipantDto participant = match.getInfo().getParticipants().stream()
                .filter(p -> p.getPuuid().equals(puuid))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("참가자 정보를 찾을 수 없습니다."));

        String kda = calculateKDA(participant.getKills(), participant.getDeaths(), participant.getAssists());
        int cs = participant.getTotalMinionsKilled() + participant.getNeutralMinionsKilled();

        return GameSummaryDto.builder()
                .matchId(match.getMetadata().getMatchId())
                .championName(participant.getChampionName())
                .kills(participant.getKills())
                .deaths(participant.getDeaths())
                .assists(participant.getAssists())
                .win(participant.isWin())
                .gameDuration(match.getInfo().getGameDuration())
                .gameMode(match.getInfo().getGameMode())
                .kda(kda)
                .cs(cs)
                .goldEarned(participant.getGoldEarned())
                .visionScore(participant.getVisionScore())
                .lane(participant.getLane())
                .role(participant.getRole())
                .build();
    }

    private String calculateKDA(int kills, int deaths, int assists) {
        if (deaths == 0) {
            return "Perfect";
        }
        double kda = (double)(kills + assists) / deaths;
        return String.format("%.2f", kda);
    }

    @Override
    public List<LeagueEntryDto> getLeagueEntriesByPuuid(String puuid) {
        log.debug("PUUID로 리그 정보 직접 조회: {}", puuid);

        System.out.println("=== 리그 정보 조회 (PUUID 직접) ===");
        System.out.println("PUUID: " + puuid);
        System.out.println("요청 URL: https://kr.api.riotgames.com/lol/league/v4/entries/by-puuid/" + puuid);

        try {
            List<LeagueEntryDto> leagueEntries = riotKrWebClient.get()
                    .uri("/lol/league/v4/entries/by-puuid/{puuid}", puuid)
                    .retrieve()
                    .bodyToFlux(LeagueEntryDto.class)
                    .collectList()
                    .block();

            // 결과 출력 코드 (완성)
            System.out.println("=== 리그 정보 결과 ===");
            System.out.println("총 " + leagueEntries.size() + "개의 리그 정보 조회됨");

            for (LeagueEntryDto entry : leagueEntries) {
                System.out.println("Queue: " + entry.getQueueType() +
                        ", Tier: " + entry.getTier() + " " + entry.getRank() +
                        ", LP: " + entry.getLeaguePoints() +
                        ", W/L: " + entry.getWins() + "/" + entry.getLosses());
            }

            return leagueEntries;

        } catch (Exception e) {
            log.error("리그 정보 조회 실패: {}", e.getMessage());
            throw new RuntimeException("리그 정보를 조회할 수 없습니다: " + puuid, e);
        }
    }
}
