package com.example.simple_lolsearch.repository;

import com.example.simple_lolsearch.entity.PlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<PlayerEntity, Long> {
    Optional<PlayerEntity> findByGameNameAndTagLine(String gameName, String tagLine);

    @Query("SELECT p FROM PlayerEntity p LEFT JOIN FETCH p.ranks WHERE p.gameName = :gameName AND p.tagLine = :tagLine")
    Optional<PlayerEntity> findByGameNameAndTagLineWithRanks(
            @Param("gameName") String gameName,
            @Param("tagLine") String tagLine
    );

    @Query("SELECT p FROM PlayerEntity p WHERE p.updatedAt < :threshold")
    List<PlayerEntity> findPlayersNeedingUpdate(@Param("threshold") LocalDateTime threshold);

    boolean existsByGameNameAndTagLine(String gameName, String tagLine);
    Optional<PlayerEntity> findByPuuid(String puuid);

    @Query("SELECT p FROM PlayerEntity p LEFT JOIN FETCH p.ranks WHERE p.puuid = :puuid")
    Optional<PlayerEntity> findByPuuidWithRanks(@Param("puuid") String puuid);
}
