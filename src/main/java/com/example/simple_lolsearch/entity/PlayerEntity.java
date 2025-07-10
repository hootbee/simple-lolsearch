package com.example.simple_lolsearch.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Entity
@Table(name="players")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "puuid", unique = true, nullable = false, length = 78)
    private String puuid;

    @Column(name = "game_name", nullable = false, length = 16)
    private String gameName;

    @Column(name = "tag_line", nullable = false, length = 5)
    private String tagLine;

    @Column(name = "summoner_id", nullable = false, length = 63)
    private String summonerId;

    @Column(name = "profile_icon_id", nullable = false)
    private Integer profileIconId = 0;

    @Column(name = "summoner_level", nullable = false)
    private Integer summonerLevel = 1;

    @Column(name = "revision_date", nullable = false)
    private Long revisionDate;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
