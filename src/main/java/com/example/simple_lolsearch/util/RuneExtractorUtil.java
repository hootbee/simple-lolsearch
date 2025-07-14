package com.example.simple_lolsearch.util;


import com.example.simple_lolsearch.dto.MatchDetailDto;
import com.example.simple_lolsearch.dto.RuneInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class RuneExtractorUtil {

    public RuneInfo extractRuneInfo(MatchDetailDto.PerksDto perks) {
        // 초기값 설정
        int keystoneId = 0;
        int primaryRuneTree = 0;
        int secondaryRuneTree = 0;
        List<Integer> runes = Arrays.asList(0, 0, 0, 0, 0, 0);
        List<Integer> statRunes = Arrays.asList(0, 0, 0);

        if (perks != null && perks.getStyles() != null && perks.getStyles().size() >= 2) {
            // 주 룬 트리
            MatchDetailDto.PerkStyleDto primaryStyle = perks.getStyles().get(0);
            primaryRuneTree = primaryStyle.getStyle();

            // 보조 룬 트리
            MatchDetailDto.PerkStyleDto secondaryStyle = perks.getStyles().get(1);
            secondaryRuneTree = secondaryStyle.getStyle();

            // 키스톤 룬
            if (primaryStyle.getSelections() != null && !primaryStyle.getSelections().isEmpty()) {
                keystoneId = primaryStyle.getSelections().get(0).getPerk();
            }

            // 전체 룬 ID 추출
            runes = perks.getStyles().stream()
                    .flatMap(style -> style.getSelections().stream())
                    .map(MatchDetailDto.PerkStyleSelectionDto::getPerk)
                    .collect(Collectors.toList());

            // 스탯 룬 정보
            if (perks.getStatPerks() != null) {
                MatchDetailDto.PerkStatsDto statPerks = perks.getStatPerks();
                statRunes = Arrays.asList(
                        statPerks.getOffense(),
                        statPerks.getFlex(),
                        statPerks.getDefense()
                );
            }
        } else {
            log.warn("룬 정보가 없거나 불완전합니다: {}", perks);
        }

        return RuneInfo.builder()
                .keystoneId(keystoneId)
                .primaryRuneTree(primaryRuneTree)
                .secondaryRuneTree(secondaryRuneTree)
                .runes(runes)
                .statRunes(statRunes)
                .build();
    }
}
