package com.example.simple_lolsearch.service.impl;

import com.example.simple_lolsearch.dto.GameSummaryDto;
import com.example.simple_lolsearch.dto.MatchDetailDto;
import com.example.simple_lolsearch.service.GameDataMapperService;
import com.example.simple_lolsearch.service.TimeFormatterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameDataMapperServiceImpl implements GameDataMapperService {

    private final TimeFormatterService timeFormatterService;

    @Override
    public GameSummaryDto mapToGameSummary(MatchDetailDto match, String puuid) {
        MatchDetailDto.ParticipantDto participant = match.getInfo().getParticipants().stream()
                .filter(p -> p.getPuuid().equals(puuid))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("참가자 정보를 찾을 수 없습니다."));

        String kda = calculateKDA(participant.getKills(), participant.getDeaths(), participant.getAssists());
        int cs = participant.getTotalMinionsKilled() + participant.getNeutralMinionsKilled();

        // 아이템 정보 처리
        List<Integer> items = Arrays.asList(
                participant.getItem0(),
                participant.getItem1(),
                participant.getItem2(),
                participant.getItem3(),
                participant.getItem4(),
                participant.getItem5()
        );

        // 시간 정보 처리 (TimeFormatterService 사용)
        long gameCreation = match.getInfo().getGameCreation();
        String absoluteDate = timeFormatterService.formatAbsoluteDate(gameCreation);
        String relativeTime = timeFormatterService.formatRelativeTime(gameCreation);
        String detailedTime = timeFormatterService.formatDetailedTime(gameCreation);

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
                .gameCreation(gameCreation)
                .gameDate(absoluteDate)
                .relativeTime(relativeTime)
                .detailedTime(detailedTime)
                .items(items)
                .trinket(participant.getItem6())
                .build();
    }

    @Override
    public String calculateKDA(int kills, int deaths, int assists) {
        if (deaths == 0) {
            return "Perfect";
        }
        double kda = (double)(kills + assists) / deaths;
        return String.format("%.2f", kda);
    }
}
