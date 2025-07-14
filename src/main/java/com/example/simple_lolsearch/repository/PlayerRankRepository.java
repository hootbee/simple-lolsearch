package com.example.simple_lolsearch.repository;

import com.example.simple_lolsearch.entity.PlayerRankEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRankRepository extends JpaRepository<PlayerRankEntity, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM PlayerRankEntity pr WHERE pr.playerEntity.puuid = :puuid")
    void deleteByPlayerEntityPuuid(@Param("puuid") String puuid);

    // 검증용 메서드
    @Query("SELECT COUNT(pr) FROM PlayerRankEntity pr WHERE pr.playerEntity.puuid = :puuid")
    long countByPlayerEntityPuuid(@Param("puuid") String puuid);
    @Query("SELECT pr FROM PlayerRankEntity pr WHERE pr.playerEntity.puuid = :puuid")
    List<PlayerRankEntity> findByPlayerEntityPuuid(@Param("puuid") String puuid);

    @Query("SELECT pr FROM PlayerRankEntity pr WHERE pr.playerEntity.puuid = :puuid AND pr.queueType = :queueType")
    Optional<PlayerRankEntity> findByPlayerEntityPuuidAndQueueType(@Param("puuid") String puuid, @Param("queueType") String queueType);
}


    // 또는 언더스코어로 명시적 구분

