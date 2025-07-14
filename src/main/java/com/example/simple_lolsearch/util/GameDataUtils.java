package com.example.simple_lolsearch.util;

import com.example.simple_lolsearch.dto.MatchDetailDto;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class GameDataUtils {

    public static List<Integer> extractItems(MatchDetailDto.ParticipantDto participant) {
        return Arrays.asList(
                participant.getItem0(),
                participant.getItem1(),
                participant.getItem2(),
                participant.getItem3(),
                participant.getItem4(),
                participant.getItem5()
        );
    }

    public static String calculateKDA(int kills, int deaths, int assists) {
        if (deaths == 0) {
            return "Perfect";
        }
        double kda = (double)(kills + assists) / deaths;
        return String.format("%.2f", kda);
    }

    public static int calculateCS(MatchDetailDto.ParticipantDto participant) {
        return participant.getTotalMinionsKilled() + participant.getNeutralMinionsKilled();
    }
}
