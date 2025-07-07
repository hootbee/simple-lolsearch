package com.example.simple_lolsearch.service.impl;

import com.example.simple_lolsearch.dto.*;
import com.example.simple_lolsearch.service.SummonerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@AllArgsConstructor
public class SummonerServiceImpl implements SummonerService {
    private final WebClient riotAsiaWebClient;
    private final WebClient riotKrWebClient;

    @Override
    public AccountDto getAccountByRiotId(String gameName, String tagLine) {
        log.debug("Riot ID로 계정 조회: {}#{}", gameName, tagLine);

        try {
            return riotAsiaWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/riot/account/v1/accounts/by-riot-id/{gameName}/{tagLine}")
                            .build(gameName, tagLine))
                    .retrieve()
                    .bodyToMono(AccountDto.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            log.warn("플레이어를 찾을 수 없습니다: {}#{}", gameName, tagLine);
            throw new RuntimeException("플레이어를 찾을 수 없습니다: " + gameName + "#" + tagLine);
        } catch (WebClientResponseException.Forbidden e) {
            log.error("API 키 권한 오류: {}#{}", gameName, tagLine);
            throw new RuntimeException("API 접근 권한이 없습니다");
        } catch (Exception e) {
            log.error("계정 조회 중 예상치 못한 오류: {}#{}", gameName, tagLine, e);
            throw new RuntimeException("계정 조회 중 오류가 발생했습니다", e);
        }
    }

    @Override
    public List<String> getRecentMatchIds(String puuid, int count) {
        log.debug("PUUID로 최근 매치 ID 조회: {}, count: {}", puuid, count);

        System.out.println("=== 최근 매치 ID 조회 ===");
        System.out.println("PUUID: " + puuid);
        System.out.println("Count: " + count);
        System.out.println("요청 URL: https://asia.api.riotgames.com/lol/match/v5/matches/by-puuid/" + puuid + "/ids?start=0&count=" + count);

        try {
            List<String> matchIds = riotAsiaWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/lol/match/v5/matches/by-puuid/{puuid}/ids")
                            .queryParam("start", 0)
                            .queryParam("count", count)
                            .build(puuid))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
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

    @Override
    public GameSummaryDto convertToGameSummary(MatchDetailDto match, String puuid) {
        MatchDetailDto.ParticipantDto participant = match.getInfo().getParticipants().stream()
                .filter(p -> p.getPuuid().equals(puuid))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("참가자 정보를 찾을 수 없습니다."));

        String kda = calculateKDA(participant.getKills(), participant.getDeaths(), participant.getAssists());
        int cs = participant.getTotalMinionsKilled() + participant.getNeutralMinionsKilled();

        // 시간 정보 처리 (개선된 버전)
        long gameCreation = match.getInfo().getGameCreation();
        String absoluteDate = formatAbsoluteDate(gameCreation);      // "2025-06-17"
        String relativeTime = formatRelativeTime(gameCreation);      // "20일 전" 또는 "30일 이전"
        String detailedTime = formatDetailedTime(gameCreation);      // "2025년 6월 17일 오후 8시 30분"

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
                .gameCreation(gameCreation)     // 타임스탬프
                .gameDate(absoluteDate)         // 절대 날짜
                .relativeTime(relativeTime)     // 상대적 시간 (새로 추가)
                .detailedTime(detailedTime)     // 상세 시간 (새로 추가)
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

    @Override
    public SummonerDto getSummonerByPuuid(String puuid) {
        log.debug("PUUID로 소환사 정보 조회: {}", puuid);

        System.out.println("=== 소환사 정보 조회 ===");
        System.out.println("PUUID: " + puuid);
        System.out.println("요청 URL: https://kr.api.riotgames.com/lol/summoner/v4/summoners/by-puuid/" + puuid);

        try {
            SummonerDto summoner = riotKrWebClient.get()
                    .uri("/lol/summoner/v4/summoners/by-puuid/{puuid}", puuid)
                    .retrieve()
                    .bodyToMono(SummonerDto.class)
                    .block();

            System.out.println("=== 소환사 정보 결과 ===");
            System.out.println("Summoner ID: " + summoner.getId());
            System.out.println("Profile Icon ID: " + summoner.getProfileIconId());
            System.out.println("Summoner Level: " + summoner.getSummonerLevel());
            System.out.println("Revision Date: " + summoner.getRevisionDate());

            return summoner;

        } catch (Exception e) {
            log.error("소환사 정보 조회 실패: {}", e.getMessage());
            throw new RuntimeException("소환사 정보를 조회할 수 없습니다: " + puuid, e);
        }
    }

    // 절대 날짜 포맷팅 ("2025-06-17")
    private String formatAbsoluteDate(long gameCreation) {
        LocalDateTime gameTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(gameCreation),
                ZoneId.of("Asia/Seoul")
        );
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return gameTime.format(formatter);
    }

    // 상대적 시간 포맷팅 ("20일 전", "3시간 전", "30일 이전")
    private String formatRelativeTime(long gameCreation) {
        LocalDateTime gameTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(gameCreation),
                ZoneId.of("Asia/Seoul")
        );

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        Duration duration = Duration.between(gameTime, now);

        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();

        System.out.println("=== 상대적 시간 계산 ===");
        System.out.println("게임 시간: " + gameTime);
        System.out.println("현재 시간: " + now);
        System.out.println("차이 - 분: " + minutes + ", 시간: " + hours + ", 일: " + days);

        if (minutes < 60) {
            String result = minutes + "분 전";
            System.out.println("결과: " + result);
            return result;
        } else if (hours < 24) {
            String result = hours + "시간 전";
            System.out.println("결과: " + result);
            return result;
        } else if (days <= 30) {
            String result = days + "일 전";
            System.out.println("결과: " + result);
            return result;
        } else {
            // 30일 초과 시 "30일 이전"으로 통일 표시
            String result = "30일 전";
            System.out.println("결과: " + result);
            return result;
        }
    }

    // 상세 시간 포맷팅 ("2025년 6월 17일 오후 8시 30분")
    private String formatDetailedTime(long gameCreation) {
        LocalDateTime gameTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(gameCreation),
                ZoneId.of("Asia/Seoul")
        );
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 a h시 m분");
        return gameTime.format(formatter);
    }
}
