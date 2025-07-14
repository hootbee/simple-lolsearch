package com.example.simple_lolsearch.service;

import com.example.simple_lolsearch.dto.GameDetailDto;
import com.example.simple_lolsearch.dto.LeagueEntryDto;
import com.example.simple_lolsearch.dto.MatchDetailDto;

import java.util.List;

public interface GameDetailMapperService {
    GameDetailDto mapToGameDetail(MatchDetailDto matchDetailDto);

    GameDetailDto.TeamDetailDto mapToTeamDetail(List<MatchDetailDto.ParticipantDto> teamParticipants, MatchDetailDto.TeamDto teamDto);

    GameDetailDto.PlayerDetailDto mapToPlayerDetail(MatchDetailDto.ParticipantDto participantDto);

    GameDetailDto.PlayerDetailDto mapToPlayerDetailWithRank(MatchDetailDto.ParticipantDto participant, LeagueEntryDto leagueEntryDto);
}
