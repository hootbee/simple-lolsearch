package com.example.simple_lolsearch.repository;

import com.example.simple_lolsearch.entity.PlayerRankEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlayerRankRepository extends JpaRepository<PlayerRankEntity, Long> {

    // ✅ 올바른 방법 - 중첩 속성 사용
    List<PlayerRankEntity> findByPlayerEntityPuuid(String puuid);

    Optional<PlayerRankEntity> findByPlayerEntityPuuidAndQueueType(String puuid, String queueType);

    void deleteByPlayerEntityPuuid(String puuid);}

    // 또는 언더스코어로 명시적 구분

