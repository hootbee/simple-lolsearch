package com.example.simple_lolsearch.service;

import com.example.simple_lolsearch.dto.PlayerProfileDto;

public interface PlayerDataService {
        /**
         * 플레이어 프로필 조회 (캐시 우선)
         *
         * 캐시된 데이터가 유효하면 캐시에서 반환하고,
         * 그렇지 않으면 API에서 최신 데이터를 조회하여 캐시에 저장 후 반환합니다.
         *
         * @param gameName 게임 내 닉네임
         * @param tagLine 태그라인 (예: KR1)
         * @return 플레이어 프로필 정보
         * @throws RuntimeException API 호출 또는 데이터 처리 중 오류 발생 시
         */
        PlayerProfileDto getPlayerProfile(String gameName, String tagLine);

        /**
         * 플레이어 프로필 강제 갱신
         *
         * 캐시 상태와 관계없이 API에서 최신 데이터를 조회하여
         * 캐시를 갱신하고 반환합니다.
         *
         * @param gameName 게임 내 닉네임
         * @param tagLine 태그라인 (예: KR1)
         * @return 갱신된 플레이어 프로필 정보
         * @throws RuntimeException API 호출 또는 데이터 처리 중 오류 발생 시
         */
        PlayerProfileDto refreshPlayerProfile(String gameName, String tagLine);

        /**
         * 오래된 캐시 데이터 정리
         *
         * 스케줄러에 의해 주기적으로 실행되어
         * 7일 이상 된 캐시 데이터를 삭제합니다.
         *
         * @Scheduled 어노테이션에 의해 1시간마다 자동 실행
         */
        void cleanupOldCache();
}
