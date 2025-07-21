package com.example.simple_lolsearch.service.impl;

import com.example.simple_lolsearch.dto.LeagueEntryDto;
import com.example.simple_lolsearch.dto.common.*;
import com.example.simple_lolsearch.dto.match.*;
import com.example.simple_lolsearch.service.TimeFormatterService;
import com.example.simple_lolsearch.util.GameDataUtils;
import com.example.simple_lolsearch.util.RuneExtractorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.example.simple_lolsearch.service.GameDetailMapperService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameDetailMapperServiceImpl implements GameDetailMapperService {

    private final TimeFormatterService timeFormatterService;
    private final RuneExtractorUtil runeExtractorUtil;

    @Override
    public GameDetailDto mapToGameDetail(MatchDetailDto match) {
        log.debug("게임 상세 정보 매핑 시작: {}", match.getMetadata().getMatchId());

        // 게임 기본 정보 생성
        BaseGameInfo gameInfo = createBaseGameInfo(match);

        List<MatchDetailDto.ParticipantDto> participants = match.getInfo().getParticipants();

        // 팀별로 플레이어 분리
        List<MatchDetailDto.ParticipantDto> blueTeamParticipants = participants.stream()
                .filter(p -> p.getTeamId() == 100)
                .collect(Collectors.toList());

        List<MatchDetailDto.ParticipantDto> redTeamParticipants = participants.stream()
                .filter(p -> p.getTeamId() == 200)
                .collect(Collectors.toList());

        // 팀 정보 조회
        MatchDetailDto.TeamDto blueTeamInfo = match.getInfo().getTeams().stream()
                .filter(t -> t.getTeamId() == 100)
                .findFirst().orElse(null);

        MatchDetailDto.TeamDto redTeamInfo = match.getInfo().getTeams().stream()
                .filter(t -> t.getTeamId() == 200)
                .findFirst().orElse(null);

        GameDetailDto.TeamDetailDto blueTeam = mapToTeamDetail(blueTeamParticipants, blueTeamInfo);
        GameDetailDto.TeamDetailDto redTeam = mapToTeamDetail(redTeamParticipants, redTeamInfo);

        return GameDetailDto.builder()
                .gameInfo(gameInfo)
                .gameDate(timeFormatterService.formatAbsoluteDate(gameInfo.getGameCreation()))
                .relativeTime(timeFormatterService.formatRelativeTime(gameInfo.getGameCreation()))
                .blueTeam(blueTeam)
                .redTeam(redTeam)
                .gameStats(calculateGameStats(participants))
                .build();
    }

    @Override
    public GameDetailDto.TeamDetailDto mapToTeamDetail(
            List<MatchDetailDto.ParticipantDto> teamParticipants,
            MatchDetailDto.TeamDto teamDto
    ) {
        if (teamDto == null) {
            log.warn("팀 정보가 null입니다.");
            return null;
        }

        List<GameDetailDto.PlayerDetailDto> players = teamParticipants.stream()
                .map(this::mapToPlayerDetail)
                .collect(Collectors.toList());

        return GameDetailDto.TeamDetailDto.builder()
                .teamId(teamDto.getTeamId())
                .win(teamDto.isWin())
                .players(players)
                .teamStats(calculateTeamStats(teamParticipants, teamDto))
                .bans(teamDto.getBans())
                .objectives(teamDto.getObjectives())
                .build();
    }

    @Override
    public GameDetailDto.PlayerDetailDto mapToPlayerDetail(MatchDetailDto.ParticipantDto participant) {
        // 공통 클래스들을 생성
        BasePlayerInfo playerInfo = createBasePlayerInfo(participant);
        GameStats gameStats = createGameStats(participant);
        ItemSpellInfo itemSpellInfo = createItemSpellInfo(participant);
        RuneInfo runeInfo = runeExtractorUtil.extractRuneInfo(participant.getPerks());

        return GameDetailDto.PlayerDetailDto.builder()
                .playerInfo(playerInfo)
                .gameStats(gameStats)
                .itemSpellInfo(itemSpellInfo)
                .runeInfo(runeInfo)
                .kda(GameDataUtils.calculateKDA(participant.getKills(), participant.getDeaths(), participant.getAssists()))
                .killParticipation(0.0)
                .cs(GameDataUtils.calculateCS(participant))
                // 랭크 정보는 기본값으로 설정
                .tier("UNRANKED")
                .rank("")
                .leaguePoints(0)
                .build();
    }

    public GameDetailDto.PlayerDetailDto mapToPlayerDetailWithRank(
            MatchDetailDto.ParticipantDto participant,
            LeagueEntryDto rankInfo
    ) {
        GameDetailDto.PlayerDetailDto basePlayer = mapToPlayerDetail(participant);

        return basePlayer.toBuilder()
                .tier(rankInfo.getTier())
                .rank(rankInfo.getRank())
                .leaguePoints(rankInfo.getLeaguePoints())
                .build();
    }

    /**
     * 킬관여율 계산을 위한 별도 메서드 (팀 정보 필요)
     */
    public GameDetailDto.PlayerDetailDto mapToPlayerDetailWithTeamInfo(
            MatchDetailDto.ParticipantDto participant,
            List<MatchDetailDto.ParticipantDto> allParticipants
    ) {
        GameDetailDto.PlayerDetailDto basePlayer = mapToPlayerDetail(participant);

        // 킬관여율 계산
        double killParticipation = calculateKillParticipation(participant, allParticipants);

        return basePlayer.toBuilder()
                .killParticipation(killParticipation)
                .build();
    }

    // === 공통 클래스 생성 메서드들 ===

    private BaseGameInfo createBaseGameInfo(MatchDetailDto match) {
        MatchDetailDto.InfoDto info = match.getInfo();
        return BaseGameInfo.builder()
                .matchId(match.getMetadata().getMatchId())
                .gameDuration(info.getGameDuration())
                .gameMode(info.getGameMode())
                .gameType(info.getGameType())
                .gameCreation(info.getGameCreation())
                .mapId(info.getMapId())
                .queueId(info.getQueueId())
                .build();
    }

    private BasePlayerInfo createBasePlayerInfo(MatchDetailDto.ParticipantDto participant) {
        return BasePlayerInfo.builder()
                .puuid(participant.getPuuid())
                .riotIdGameName(getDisplayName(participant))
                .riotIdTagline(participant.getRiotIdTagline())
                .championName(participant.getChampionName())
                .championId(participant.getChampionId())
                .lane(participant.getLane())
                .role(participant.getRole())
                .build();
    }

    private GameStats createGameStats(MatchDetailDto.ParticipantDto participant) {
        return GameStats.builder()
                .kills(participant.getKills())
                .deaths(participant.getDeaths())
                .assists(participant.getAssists())
                .goldEarned(participant.getGoldEarned())
                .champLevel(participant.getChampLevel())
                .totalDamageDealtToChampions(participant.getTotalDamageDealtToChampions())
                .totalDamageTaken(participant.getTotalDamageTaken())
                .largestMultiKill(participant.getLargestMultiKill())
                .visionScore(participant.getVisionScore())
                .win(participant.isWin())
                .build();
    }

    private ItemSpellInfo createItemSpellInfo(MatchDetailDto.ParticipantDto participant) {
        List<Integer> items = GameDataUtils.extractItems(participant);

        return ItemSpellInfo.builder()
                .items(items)
                .trinket(participant.getItem6())
                .summonerSpell1Id(participant.getSummoner1Id())
                .summonerSpell2Id(participant.getSummoner2Id())
                .build();
    }

    // === 계산 메서드들 ===

    private GameDetailDto.GameStatsDto calculateGameStats(List<MatchDetailDto.ParticipantDto> participants) {
        int totalKills = participants.stream().mapToInt(MatchDetailDto.ParticipantDto::getKills).sum();
        int totalDeaths = participants.stream().mapToInt(MatchDetailDto.ParticipantDto::getDeaths).sum();
        int totalAssists = participants.stream().mapToInt(MatchDetailDto.ParticipantDto::getAssists).sum();

        return GameDetailDto.GameStatsDto.builder()
                .totalKills(totalKills)
                .totalDeaths(totalDeaths)
                .totalAssists(totalAssists)
                .build();
    }

    private GameDetailDto.TeamStatsDto calculateTeamStats(
            List<MatchDetailDto.ParticipantDto> teamParticipants,
            MatchDetailDto.TeamDto teamDto
    ) {
        int totalKills = teamParticipants.stream().mapToInt(MatchDetailDto.ParticipantDto::getKills).sum();
        int totalDeaths = teamParticipants.stream().mapToInt(MatchDetailDto.ParticipantDto::getDeaths).sum();
        int totalAssists = teamParticipants.stream().mapToInt(MatchDetailDto.ParticipantDto::getAssists).sum();
        int totalGold = teamParticipants.stream().mapToInt(MatchDetailDto.ParticipantDto::getGoldEarned).sum();
        int totalDamage = teamParticipants.stream().mapToInt(MatchDetailDto.ParticipantDto::getTotalDamageDealtToChampions).sum();

        MatchDetailDto.ObjectivesDto objectives = teamDto.getObjectives();

        return GameDetailDto.TeamStatsDto.builder()
                .totalKills(totalKills)
                .totalDeaths(totalDeaths)
                .totalAssists(totalAssists)
                .totalGold(totalGold)
                .totalDamage(totalDamage)
                .baronKills(objectives != null && objectives.getBaron() != null ? objectives.getBaron().getKills() : 0)
                .dragonKills(objectives != null && objectives.getDragon() != null ? objectives.getDragon().getKills() : 0)
                .riftHeraldKills(objectives != null && objectives.getRiftHerald() != null ? objectives.getRiftHerald().getKills() : 0)
                .towerKills(objectives != null && objectives.getTower() != null ? objectives.getTower().getKills() : 0)
                .inhibitorKills(objectives != null && objectives.getInhibitor() != null ? objectives.getInhibitor().getKills() : 0)
                .firstBlood(objectives != null && objectives.getChampion() != null ? objectives.getChampion().isFirst() : false)
                .build();
    }

    private double calculateKillParticipation(
            MatchDetailDto.ParticipantDto participant,
            List<MatchDetailDto.ParticipantDto> allParticipants
    ) {
        int teamTotalKills = allParticipants.stream()
                .filter(p -> p.getTeamId() == participant.getTeamId())
                .mapToInt(MatchDetailDto.ParticipantDto::getKills)
                .sum();

        if (teamTotalKills == 0) {
            return 0.0;
        }

        double participation = (double)(participant.getKills() + participant.getAssists()) / teamTotalKills * 100;
        return Math.round(participation * 10.0) / 10.0;
    }

    private String getDisplayName(MatchDetailDto.ParticipantDto participant) {
        // Riot ID가 있으면 사용
        if (participant.getRiotIdGameName() != null &&
                !participant.getRiotIdGameName().trim().isEmpty()) {
            String tagline = participant.getRiotIdTagline();
            if (tagline != null && !tagline.trim().isEmpty()) {
                return participant.getRiotIdGameName() + "#" + tagline;
            }
            return participant.getRiotIdGameName();
        }

        // 둘 다 없으면 PUUID의 일부 사용
        String puuid = participant.getPuuid();
        if (puuid != null && puuid.length() > 8) {
            return "Player_" + puuid.substring(0, 8);
        }

        return "Unknown Player";
    }
}
