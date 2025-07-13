package com.example.simple_lolsearch.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "match_details")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchDetailEntity {

    @Id
    @Column(name = "match_id", length = 50)
    private String matchId;

    // 메타데이터 정보
    @Column(name = "data_version", length = 20)
    private String dataVersion;

    @Column(name = "participants_puuids", columnDefinition = "JSON")
    private String participantsPuuids;  // List<String>을 JSON으로 저장

    // 게임 기본 정보
    @Column(name = "game_creation")
    private Long gameCreation;

    @Column(name = "game_duration")
    private Long gameDuration;

    @Column(name = "game_mode", length = 50)
    private String gameMode;

    @Column(name = "game_type", length = 50)
    private String gameType;

    @Column(name = "game_version", length = 20)
    private String gameVersion;

    @Column(name = "map_id")
    private Integer mapId;

    // 복잡한 데이터는 JSON으로 저장
    @Column(name = "participants_data", columnDefinition = "JSON")
    private String participantsData;  // List<ParticipantDto>

    @Column(name = "teams_data", columnDefinition = "JSON")
    private String teamsData;  // List<TeamDto>

    // 검색 최적화용 필드들
    @Column(name = "total_kills")
    private Integer totalKills;

    @Column(name = "game_duration_minutes")
    private Integer gameDurationMinutes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    private void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;

        // 검색용 필드 계산
        if (gameDuration != null) {
            gameDurationMinutes = (int) (gameDuration / 60);
        }
    }

    @PreUpdate
    private void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

