package com.example.simple_lolsearch.repository;

import com.example.simple_lolsearch.entity.PlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<PlayerEntity, Long> {
    Optional<PlayerEntity> findByGameNameAndTagLine(String gameName, String tagLine);

    Optional<PlayerEntity> findByPuuid(String puuid);

    boolean existsByPuuid(String puuid);

    boolean existsByGameNameAndTagLine(String gameName, String tagLine);

}
