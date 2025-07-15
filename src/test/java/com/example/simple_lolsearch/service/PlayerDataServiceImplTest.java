//package com.example.simple_lolsearch.service;
//
//import com.example.simple_lolsearch.dto.AccountDto;
//import com.example.simple_lolsearch.dto.LeagueEntryDto;
//import com.example.simple_lolsearch.dto.PlayerProfileDto;
//import com.example.simple_lolsearch.entity.PlayerEntity;
//import com.example.simple_lolsearch.entity.PlayerRankEntity;
//import com.example.simple_lolsearch.repository.PlayerRankRepository;
//import com.example.simple_lolsearch.repository.PlayerRepository;
//import com.example.simple_lolsearch.service.SummonerService;
//import com.example.simple_lolsearch.service.impl.PlayerDataServiceImpl;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.never;
//import static org.mockito.Mockito.verify;  // ✅ 이렇게 수정
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//@DisplayName("PlayerDataServiceImpl 단위 테스트")
//class PlayerDataServiceImplTest {
//
//    @Mock
//    private PlayerRepository playerRepository;
//
//    @Mock
//    private PlayerRankRepository playerRankRepository;
//
//    @Mock
//    private SummonerService summonerService;
//
//    @InjectMocks
//    private PlayerDataServiceImpl playerDataService;
//
//    @Test
//    @DisplayName("캐시된 데이터가 유효한 경우 캐시 사용")
//    void getPlayerProfile_UseCache_WhenCacheIsValid() {
//        // given
//        String gameName = "TestPlayer";
//        String tagLine = "KR1";
//
//        PlayerEntity cachedPlayer = createMockPlayerEntity();
//        cachedPlayer.setUpdatedAt(LocalDateTime.now().minusMinutes(30)); // 30분 전 업데이트
//
//        when(playerRepository.findByGameNameAndTagLineWithRanks(gameName, tagLine))
//                .thenReturn(Optional.of(cachedPlayer));
//
//        // when
//        PlayerProfileDto result = playerDataService.getPlayerProfile(gameName, tagLine);
//
//        // then
//        assertThat(result).isNotNull();
//        assertThat(result.getAccount().getGameName()).isEqualTo(gameName);
//        assertThat(result.getAccount().getTagLine()).isEqualTo(tagLine);
//
//        // API 호출이 없었는지 확인
//        verify(summonerService, never()).getAccountByRiotId(any(), any());
//        verify(playerRepository, never()).save(any());
//    }
//
//    @Test
//    @DisplayName("캐시가 만료된 경우 API에서 새 데이터 조회")
//    void getPlayerProfile_FetchFromAPI_WhenCacheExpired() {
//        // given
//        String gameName = "TestPlayer";
//        String tagLine = "KR1";
//
//        PlayerEntity expiredPlayer = createMockPlayerEntity();
//        expiredPlayer.setUpdatedAt(LocalDateTime.now().minusHours(2)); // 2시간 전 업데이트 (만료)
//
//        when(playerRepository.findByGameNameAndTagLineWithRanks(gameName, tagLine))
//                .thenReturn(Optional.of(expiredPlayer));
//
//        setupMockApiResponses(gameName, tagLine);
//
//        // when
//        PlayerProfileDto result = playerDataService.getPlayerProfile(gameName, tagLine);
//
//        // then
//        assertThat(result).isNotNull();
//        verify(summonerService).getAccountByRiotId(gameName, tagLine);
//        verify(playerRepository).save(any(PlayerEntity.class));
//    }
//
//    @Test
//    @DisplayName("캐시가 없는 경우 API에서 데이터 조회")
//    void getPlayerProfile_FetchFromAPI_WhenNoCache() {
//        // given
//        String gameName = "TestPlayer";
//        String tagLine = "KR1";
//
//        when(playerRepository.findByGameNameAndTagLineWithRanks(gameName, tagLine))
//                .thenReturn(Optional.empty());
//
//        setupMockApiResponses(gameName, tagLine);
//
//        // when
//        PlayerProfileDto result = playerDataService.getPlayerProfile(gameName, tagLine);
//
//        // then
//        assertThat(result).isNotNull();
//        verify(summonerService).getAccountByRiotId(gameName, tagLine);
//        verify(playerRepository).save(any(PlayerEntity.class));
//    }
//
//    @Test
//    @DisplayName("프로필 강제 갱신 테스트")
//    void refreshPlayerProfile_Success() {
//        // given
//        String gameName = "TestPlayer";
//        String tagLine = "KR1";
//
//        setupMockApiResponses(gameName, tagLine);
//
//        // when
//        PlayerProfileDto result = playerDataService.refreshPlayerProfile(gameName, tagLine);
//
//        // then
//        assertThat(result).isNotNull();
//        verify(summonerService).getAccountByRiotId(gameName, tagLine);
//        verify(playerRepository).save(any(PlayerEntity.class));
//        // 캐시 확인 없이 바로 API 호출되었는지 확인
//        verify(playerRepository, never()).findByGameNameAndTagLineWithRanks(any(), any());
//    }
//
//    @Test
//    @DisplayName("새 플레이어 저장 테스트")
//    void saveOrUpdatePlayer_NewPlayer() {
//        // given
//        String gameName = "TestPlayer";
//        String tagLine = "KR1";
//
//        // 실제로 필요한 Mock 설정만 유지
//        when(playerRepository.findByGameNameAndTagLineWithRanks(gameName, tagLine))
//                .thenReturn(Optional.empty());
//
//        setupMockApiResponses(gameName, tagLine);
//
//        // when
//        PlayerProfileDto result = playerDataService.getPlayerProfile(gameName, tagLine);
//
//        // then
//        assertThat(result).isNotNull();
//        verify(summonerService).getAccountByRiotId(gameName, tagLine);
//        verify(playerRepository).save(any(PlayerEntity.class));
//        verify(playerRankRepository).deleteByPlayerPuuid(any());
//        verify(playerRankRepository).saveAll(any());
//    }
//
//    @Test
//    @DisplayName("기존 플레이어 업데이트 테스트")
//    void saveOrUpdatePlayer_ExistingPlayer() {
//        // given
//        String gameName = "TestPlayer";
//        String tagLine = "KR1";
//
//        // 캐시가 없거나 만료된 상황 설정
//        when(playerRepository.findByGameNameAndTagLineWithRanks(gameName, tagLine))
//                .thenReturn(Optional.empty());
//
//        setupMockApiResponses(gameName, tagLine);
//
//        // when
//        PlayerProfileDto result = playerDataService.getPlayerProfile(gameName, tagLine);
//
//        // then
//        assertThat(result).isNotNull();
//        verify(playerRepository).save(any(PlayerEntity.class));
//        verify(playerRankRepository).deleteByPlayerPuuid(any());
//        verify(playerRankRepository).saveAll(any());
//    }
//
//    @Test
//    @DisplayName("API 호출 실패 시 예외 처리")
//    void getPlayerProfile_ThrowException_WhenAPIFails() {
//        // given
//        String gameName = "TestPlayer";
//        String tagLine = "KR1";
//
//        when(playerRepository.findByGameNameAndTagLineWithRanks(gameName, tagLine))
//                .thenReturn(Optional.empty());
//
//        when(summonerService.getAccountByRiotId(gameName, tagLine))
//                .thenThrow(new RuntimeException("API 호출 실패"));
//
//        // when & then
//        assertThatThrownBy(() -> playerDataService.getPlayerProfile(gameName, tagLine))
//                .isInstanceOf(RuntimeException.class)
//                .hasMessageContaining("플레이어 프로필 처리 중 오류가 발생했습니다");
//    }
//
//    @Test
//    @DisplayName("오래된 캐시 정리 테스트")
//    void cleanupOldCache_Success() {
//        // given
//        List<PlayerEntity> oldPlayers = List.of(
//                createMockPlayerEntity(),
//                createMockPlayerEntity()
//        );
//
//        when(playerRepository.findPlayersNeedingUpdate(any(LocalDateTime.class)))
//                .thenReturn(oldPlayers);
//
//        // when
//        playerDataService.cleanupOldCache();
//
//        // then
//        verify(playerRepository).findPlayersNeedingUpdate(any(LocalDateTime.class));
//        verify(playerRepository).deleteAll(oldPlayers);
//    }
//
//    @Test
//    @DisplayName("정리할 오래된 캐시가 없는 경우")
//    void cleanupOldCache_NoOldData() {
//        // given
//        when(playerRepository.findPlayersNeedingUpdate(any(LocalDateTime.class)))
//                .thenReturn(List.of());
//
//        // when
//        playerDataService.cleanupOldCache();
//
//        // then
//        verify(playerRepository).findPlayersNeedingUpdate(any(LocalDateTime.class));
//        verify(playerRepository, never()).deleteAll(any());
//    }
//
//    // Helper 메서드들
//    private void setupMockApiResponses(String gameName, String tagLine) {
//        AccountDto account = createMockAccountDto();
//        SummonerDto summoner = createMockSummonerDto();
//        List<LeagueEntryDto> leagueEntries = createMockLeagueEntries();
//        PlayerEntity savedPlayer = createMockPlayerEntity();
//
//        when(summonerService.getAccountByRiotId(gameName, tagLine)).thenReturn(account);
//        when(summonerService.getSummonerByPuuid(any())).thenReturn(summoner);
//        when(summonerService.getLeagueEntriesByPuuid(any())).thenReturn(leagueEntries);
//        when(playerRepository.findByGameNameAndTagLine(any(), any())).thenReturn(Optional.empty());
//        when(playerRepository.save(any(PlayerEntity.class))).thenReturn(savedPlayer);
//    }
//
//    private PlayerEntity createMockPlayerEntity() {
//        PlayerEntity player = PlayerEntity.builder()
//                .puuid("test-puuid-123")
//                .gameName("TestPlayer")
//                .tagLine("KR1")
//                .summonerId("summoner-id-123")
//                .summonerLevel(100)
//                .profileIconId(1)
//                .revisionDate(System.currentTimeMillis())
//                .build();
//
//        player.setUpdatedAt(LocalDateTime.now());
//
//        // Mock ranks
//        List<PlayerRankEntity> ranks = List.of(
//                PlayerRankEntity.builder()
//                        .playerEntity(player)
//                        .queueType("RANKED_SOLO_5x5")
//                        .tier("GOLD")
//                        .rankDivision("II")
//                        .leaguePoints(75)
//                        .wins(50)
//                        .losses(30)
//                        .build()
//        );
//        player.setRanks(ranks);
//
//        return player;
//    }
//
//    private AccountDto createMockAccountDto() {
//        return AccountDto.builder()
//                .puuid("test-puuid-123")
//                .gameName("TestPlayer")
//                .tagLine("KR1")
//                .build();
//    }
//
//    private SummonerDto createMockSummonerDto() {
//        return SummonerDto.builder()
//                .id("summoner-id-123")
//                .puuid("test-puuid-123")
//                .summonerLevel(100)
//                .profileIconId(1)
//                .revisionDate(System.currentTimeMillis())
//                .build();
//    }
//
//    private List<LeagueEntryDto> createMockLeagueEntries() {
//        return List.of(
//                LeagueEntryDto.builder()
//                        .queueType("RANKED_SOLO_5x5")
//                        .tier("GOLD")
//                        .rank("II")
//                        .leaguePoints(75)
//                        .wins(50)
//                        .losses(30)
//                        .hotStreak(false)
//                        .veteran(false)
//                        .freshBlood(false)
//                        .inactive(false)
//                        .build()
//        );
//    }
//}
