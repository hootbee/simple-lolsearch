package com.example.simple_lolsearch.repository;

import com.example.simple_lolsearch.dto.MatchDetailDto;
import com.example.simple_lolsearch.entity.MatchDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MatchDetailRepository extends JpaRepository<MatchDetailEntity, String> {
    Optional<MatchDetailEntity> findByMatchId(String matchId);

    // 게임 모드별 조회
    List<MatchDetailEntity> findByGameModeOrderByGameCreationDesc(String gameMode);

    // 특정 기간 게임 조회
    List<MatchDetailEntity> findByGameCreationBetween(Long startTime, Long endTime);

    // 최근 게임 조회
    List<MatchDetailEntity> findTop10ByOrderByGameCreationDesc();

    // 존재 여부 확인
    boolean existsByMatchId(String matchId);

    // 오래된 데이터 조회 (정리용)
    @Query("SELECT m FROM MatchDetailEntity m WHERE m.updatedAt < :threshold")
    List<MatchDetailEntity> findMatchesNeedingUpdate(@Param("threshold") LocalDateTime threshold);

    // 특정 플레이어가 참가한 게임 조회 (JSON 쿼리 활용)
    @Query(value = "SELECT * FROM match_details WHERE JSON_CONTAINS(participants_puuids, JSON_QUOTE(:puuid))", nativeQuery = true)
    List<MatchDetailEntity> findByParticipantPuuid(@Param("puuid") String puuid);
}
