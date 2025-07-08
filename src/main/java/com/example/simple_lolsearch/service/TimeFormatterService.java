package com.example.simple_lolsearch.service;

public interface TimeFormatterService {
    String formatAbsoluteDate(long timestamp);
    String formatRelativeTime(long timestamp);
    String formatDetailedTime(long timestamp);
}
