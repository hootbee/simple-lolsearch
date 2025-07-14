package com.example.simple_lolsearch.entity;

import com.example.simple_lolsearch.entity.PlayerRankEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "players")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "ranks")
@ToString(exclude = "ranks")
public class PlayerEntity {

    @Id
    @Column(name = "puuid", unique = true, nullable = false, length = 78)
    private String puuid;

    @Column(name = "game_name", nullable = false)
    private String gameName;

    @Column(name = "tag_line", nullable = false)
    private String tagLine;

    @Column(name = "summoner_id")
    private String summonerId;

    @Column(name = "summoner_level")
    private Integer summonerLevel;

    @Column(name = "profile_icon_id")
    private Integer profileIconId;

    @Column(name = "revision_date")
    private Long revisionDate;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "playerEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayerRankEntity> ranks = new ArrayList<>();

    @PrePersist
    private void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    private void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
