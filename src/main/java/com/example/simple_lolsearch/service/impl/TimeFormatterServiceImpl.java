package com.example.simple_lolsearch.service.impl;

import com.example.simple_lolsearch.service.TimeFormatterService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class TimeFormatterServiceImpl implements TimeFormatterService {

    private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");

    @Override
    public String formatAbsoluteDate(long timestamp) {
        LocalDateTime gameTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp),
                KOREA_ZONE
        );
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return gameTime.format(formatter);
    }

    @Override
    public String formatRelativeTime(long timestamp) {
        LocalDateTime gameTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp),
                KOREA_ZONE
        );

        LocalDateTime now = LocalDateTime.now(KOREA_ZONE);
        Duration duration = Duration.between(gameTime, now);

        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();

        if (minutes < 60) {
            return minutes + "분 전";
        } else if (hours < 24) {
            return hours + "시간 전";
        } else if (days <= 30) {
            return days + "일 전";
        } else {
            return "30일 전";
        }
    }

    @Override
    public String formatDetailedTime(long timestamp) {
        LocalDateTime gameTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp),
                KOREA_ZONE
        );
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 a h시 m분");
        return gameTime.format(formatter);
    }
}
