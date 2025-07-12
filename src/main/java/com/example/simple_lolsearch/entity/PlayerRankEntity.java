package com.example.simple_lolsearch.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "player_ranks",
        uniqueConstraints = @UniqueConstraint(name = "uq_player_queue", columnNames = {"player_entity_id", "queue_type"})  // ✅ 수정: player_id → player_entity_id
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerRankEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_entity_id", nullable = false)  // ✅ 이미 올바름
    private PlayerEntity playerEntity;

    @Column(name = "queue_type", nullable = false)
    private String queueType;

    @Column(name = "tier")
    private String tier;

    @Column(name = "rank_division")
    private String rankDivision;

    @Column(name = "league_points")
    private Integer leaguePoints;

    @Column(name = "wins")
    private Integer wins;

    @Column(name = "losses")
    private Integer losses;

    @Builder.Default
    @Column(name = "hot_streak", nullable = false)  // ✅ 추가: nullable = false
    private Boolean hotStreak = false;

    @Builder.Default
    @Column(name = "veteran", nullable = false)  // ✅ 추가: nullable = false
    private Boolean veteran = false;

    @Builder.Default
    @Column(name = "fresh_blood", nullable = false)  // ✅ 추가: nullable = false
    private Boolean freshBlood = false;

    @Builder.Default
    @Column(name = "inactive", nullable = false)  // ✅ 추가: nullable = false
    private Boolean inactive = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

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
