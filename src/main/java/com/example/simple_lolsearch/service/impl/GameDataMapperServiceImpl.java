package com.example.simple_lolsearch.service.impl;

import com.example.simple_lolsearch.dto.GameSummaryDto;
import com.example.simple_lolsearch.dto.MatchDetailDto;
import com.example.simple_lolsearch.service.GameDataMapperService;
import com.example.simple_lolsearch.service.TimeFormatterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

        // === 실제 API 구조에서 룬 정보 추출 ===
        MatchDetailDto.PerksDto perks = participant.getPerks();

        // 초기값 설정
        int keystoneId = 0;
        int primaryRuneTree = 0;
        int secondaryRuneTree = 0;
        List<Integer> runes = Arrays.asList(0, 0, 0, 0, 0, 0);
        List<Integer> statRunes = Arrays.asList(0, 0, 0);

        // perks 객체가 존재하는지 확인
        if (perks != null) {
            System.out.println("=== Perks 객체 존재 확인 ===");
            System.out.println("perks: " + perks);
            System.out.println("styles: " + perks.getStyles());
            System.out.println("statPerks: " + perks.getStatPerks());

            // 룬 스타일 정보 추출 (주 룬 트리, 보조 룬 트리)
            if (perks.getStyles() != null && perks.getStyles().size() >= 2) {
                // 주 룬 트리 (첫 번째 style)
                MatchDetailDto.PerkStyleDto primaryStyle = perks.getStyles().get(0);
                primaryRuneTree = primaryStyle.getStyle();

                // 보조 룬 트리 (두 번째 style)
                MatchDetailDto.PerkStyleDto secondaryStyle = perks.getStyles().get(1);
                secondaryRuneTree = secondaryStyle.getStyle();

                // 키스톤 룬 추출 (주 룬 트리의 첫 번째 selection)
                if (primaryStyle.getSelections() != null && !primaryStyle.getSelections().isEmpty()) {
                    keystoneId = primaryStyle.getSelections().get(0).getPerk();
                }

                // 전체 룬 ID 추출 (주 룬 트리 + 보조 룬 트리의 모든 selections)
                runes = perks.getStyles().stream()
                        .flatMap(style -> style.getSelections().stream())
                        .map(MatchDetailDto.PerkStyleSelectionDto::getPerk)
                        .collect(Collectors.toList());

                System.out.println("=== 룬 스타일 추출 결과 ===");
                System.out.println("주 룬 트리 ID: " + primaryRuneTree);
                System.out.println("보조 룬 트리 ID: " + secondaryRuneTree);
                System.out.println("키스톤 ID: " + keystoneId);
                System.out.println("전체 룬 목록: " + runes);
            }

            // 스탯 룬 정보 추출
            if (perks.getStatPerks() != null) {
                MatchDetailDto.PerkStatsDto statPerks = perks.getStatPerks();
                statRunes = Arrays.asList(
                        statPerks.getOffense(),  // 공격 스탯 룬
                        statPerks.getFlex(),     // 적응형 스탯 룬
                        statPerks.getDefense()   // 방어 스탯 룬
                );

                System.out.println("=== 스탯 룬 추출 결과 ===");
                System.out.println("공격 스탯 룬: " + statPerks.getOffense());
                System.out.println("적응형 스탯 룬: " + statPerks.getFlex());
                System.out.println("방어 스탯 룬: " + statPerks.getDefense());
                System.out.println("스탯 룬 목록: " + statRunes);
            }
        } else {
            System.out.println("=== 경고: Perks 객체가 null입니다 ===");
            System.out.println("participant.getPerks() 결과: " + participant.getPerks());
        }

        // 시간 정보 처리
        long gameCreation = match.getInfo().getGameCreation();
        String absoluteDate = timeFormatterService.formatAbsoluteDate(gameCreation);
        String relativeTime = timeFormatterService.formatRelativeTime(gameCreation);
        String detailedTime = timeFormatterService.formatDetailedTime(gameCreation);

        // 최종 디버깅 로그
        System.out.println("=== 최종 룬 데이터 매핑 결과 ===");
        System.out.println("키스톤 ID: " + keystoneId);
        System.out.println("주 룬 트리: " + primaryRuneTree);
        System.out.println("보조 룬 트리: " + secondaryRuneTree);
        System.out.println("전체 룬: " + runes);
        System.out.println("스탯 룬: " + statRunes);
        System.out.println("소환사 주문: " + participant.getSummoner1Id() + ", " + participant.getSummoner2Id());

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

                // 스펠 정보 매핑
                .summonerSpell1Id(participant.getSummoner1Id())
                .summonerSpell2Id(participant.getSummoner2Id())

                // 실제 API 구조에서 추출한 룬 정보 매핑
                .keystoneId(keystoneId)
                .primaryRuneTree(primaryRuneTree)
                .secondaryRuneTree(secondaryRuneTree)
                .runes(runes)
                .statRunes(statRunes)
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
