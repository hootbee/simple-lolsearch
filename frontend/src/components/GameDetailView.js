/* src/components/GameDetailView.jsx */
import React from 'react';
import styled from 'styled-components';
import ChampionImage from './ChampionImage';
import ItemBuild from './ItemBuild';
import SpellRuneDisplay from './SpellRuneDisplay';

const DetailContainer = styled.div`
    padding: 20px;
    background: white;
    margin: 10px;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
`;

const GameInfo = styled.div`
    text-align: center;
    margin-bottom: 20px;
    padding-bottom: 15px;
    border-bottom: 2px solid #eee;
`;

const GameTitle = styled.h2`
    margin: 0 0 10px 0;
    color: #333;
    font-size: 1.5rem;
`;

const GameMeta = styled.div`
    color: #666;
    font-size: 0.9rem;
`;

const TeamsContainer = styled.div`
    display: flex;
    gap: 20px;
    
    @media (max-width: 768px) {
        flex-direction: column;
    }
`;

const TeamSection = styled.div`
    flex: 1;
    background: ${({ win }) => (win ? '#f0f8f0' : '#f8f0f0')};
    border-radius: 8px;
    padding: 15px;
    border: 2px solid ${({ win }) => (win ? '#4caf50' : '#f44336')};
`;

const TeamHeader = styled.div`
    text-align: center;
    margin-bottom: 15px;
    padding-bottom: 10px;
    border-bottom: 1px solid ${({ win }) => (win ? '#4caf50' : '#f44336')};
`;

const TeamTitle = styled.h3`
    margin: 0;
    color: ${({ win }) => (win ? '#4caf50' : '#f44336')};
    font-size: 1.2rem;
`;

const TeamStats = styled.div`
    display: flex;
    justify-content: space-around;
    margin-top: 5px;
    font-size: 0.9rem;
    color: #666;
`;

const PlayersGrid = styled.div`
    display: flex;
    flex-direction: column;
    gap: 8px;
`;

const PlayerRow = styled.div`
    display: grid;
    grid-template-columns: 40px 1fr 80px 100px 60px 80px;
    align-items: center;
    gap: 10px;
    padding: 8px;
    background: white;
    border-radius: 4px;
    font-size: 0.85rem;
    
    &:hover {
        background: #f8f9fa;
    }
    
    @media (max-width: 768px) {
        grid-template-columns: 30px 1fr 60px 80px;
        gap: 5px;
        font-size: 0.8rem;
    }
`;

const PlayerName = styled.div`
    font-weight: bold;
    color: #333;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
`;

const PlayerKDA = styled.div`
    text-align: center;
    color: #666;
`;

const PlayerItems = styled.div`
    display: flex;
    gap: 2px;
`;

const PlayerRank = styled.div`
    text-align: center;
    font-size: 0.8rem;
    color: #888;
`;

const ObjectiveStats = styled.div`
    margin-top: 10px;
    padding-top: 10px;
    border-top: 1px solid #eee;
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(80px, 1fr));
    gap: 10px;
    text-align: center;
    font-size: 0.8rem;
`;

const ObjectiveItem = styled.div`
    color: #666;
    
    strong {
        display: block;
        color: #333;
        font-size: 1.1em;
    }
`;

const GameDetailView = ({ gameDetail }) => {
    const formatDuration = (seconds) => {
        const minutes = Math.floor(seconds / 60);
        const remainingSeconds = seconds % 60;
        return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`;
    };

    return (
        <DetailContainer>
            <GameInfo>
                <GameTitle>{gameDetail.gameMode}</GameTitle>
                <GameMeta>
                    게임 시간: {formatDuration(gameDetail.gameDuration)} |
                    {gameDetail.gameDate} |
                    매치 ID: {gameDetail.matchId}
                </GameMeta>
            </GameInfo>

            <TeamsContainer>
                {/* 블루팀 */}
                <TeamSection win={gameDetail.blueTeam.win}>
                    <TeamHeader win={gameDetail.blueTeam.win}>
                        <TeamTitle win={gameDetail.blueTeam.win}>
                            블루팀 {gameDetail.blueTeam.win ? '(승리)' : '(패배)'}
                        </TeamTitle>
                        <TeamStats win={gameDetail.blueTeam.win}>
                            <span>킬: {gameDetail.blueTeam.teamStats.totalKills}</span>
                            <span>골드: {gameDetail.blueTeam.teamStats.totalGold.toLocaleString()}</span>
                        </TeamStats>
                    </TeamHeader>

                    <PlayersGrid>
                        {gameDetail.blueTeam.players.map((player, index) => (
                            <PlayerRow key={index}>
                                <ChampionImage
                                    championName={player.championName}
                                    size="32px"
                                />
                                <div>
                                    <PlayerName>{player.riotIdGameName || 'Unknown'}</PlayerName>
                                    <div style={{fontSize: '0.75rem', color: '#888'}}>
                                        {player.championName}
                                    </div>
                                </div>
                                <PlayerKDA>
                                    {player.kills}/{player.deaths}/{player.assists}
                                </PlayerKDA>
                                <PlayerItems>
                                    <ItemBuild
                                        items={player.items}
                                        trinket={player.trinket}
                                        size={16}
                                    />
                                </PlayerItems>
                                <div style={{textAlign: 'center'}}>
                                    CS: {player.cs}
                                </div>
                                <PlayerRank>
                                    {player.tier} {player.rank}
                                </PlayerRank>
                            </PlayerRow>
                        ))}
                    </PlayersGrid>

                    <ObjectiveStats>
                        <ObjectiveItem>
                            <strong>{gameDetail.blueTeam.teamStats.baronKills}</strong>
                            바론
                        </ObjectiveItem>
                        <ObjectiveItem>
                            <strong>{gameDetail.blueTeam.teamStats.dragonKills}</strong>
                            드래곤
                        </ObjectiveItem>
                        <ObjectiveItem>
                            <strong>{gameDetail.blueTeam.teamStats.towerKills}</strong>
                            타워
                        </ObjectiveItem>
                        <ObjectiveItem>
                            <strong>{gameDetail.blueTeam.teamStats.inhibitorKills}</strong>
                            억제기
                        </ObjectiveItem>
                    </ObjectiveStats>
                </TeamSection>

                {/* 레드팀 */}
                <TeamSection win={gameDetail.redTeam.win}>
                    <TeamHeader win={gameDetail.redTeam.win}>
                        <TeamTitle win={gameDetail.redTeam.win}>
                            레드팀 {gameDetail.redTeam.win ? '(승리)' : '(패배)'}
                        </TeamTitle>
                        <TeamStats win={gameDetail.redTeam.win}>
                            <span>킬: {gameDetail.redTeam.teamStats.totalKills}</span>
                            <span>골드: {gameDetail.redTeam.teamStats.totalGold.toLocaleString()}</span>
                        </TeamStats>
                    </TeamHeader>

                    <PlayersGrid>
                        {gameDetail.redTeam.players.map((player, index) => (
                            <PlayerRow key={index}>
                                <ChampionImage
                                    championName={player.championName}
                                    size="32px"
                                />
                                <div>
                                    <PlayerName>{player.riotIdGameName|| 'Unknsdfdfaown'}</PlayerName>
                                    <div style={{fontSize: '0.75rem', color: '#888'}}>
                                        {player.championName}
                                    </div>
                                </div>
                                <PlayerKDA>
                                    {player.kills}/{player.deaths}/{player.assists}
                                </PlayerKDA>
                                <PlayerItems>
                                    <ItemBuild
                                        items={player.items}
                                        trinket={player.trinket}
                                        size={16}
                                    />
                                </PlayerItems>
                                <div style={{textAlign: 'center'}}>
                                    CS: {player.cs}
                                </div>
                                <PlayerRank>
                                    {player.tier} {player.rank}
                                </PlayerRank>
                            </PlayerRow>
                        ))}
                    </PlayersGrid>

                    <ObjectiveStats>
                        <ObjectiveItem>
                            <strong>{gameDetail.redTeam.teamStats.baronKills}</strong>
                            바론
                        </ObjectiveItem>
                        <ObjectiveItem>
                            <strong>{gameDetail.redTeam.teamStats.dragonKills}</strong>
                            드래곤
                        </ObjectiveItem>
                        <ObjectiveItem>
                            <strong>{gameDetail.redTeam.teamStats.towerKills}</strong>
                            타워
                        </ObjectiveItem>
                        <ObjectiveItem>
                            <strong>{gameDetail.redTeam.teamStats.inhibitorKills}</strong>
                            억제기
                        </ObjectiveItem>
                    </ObjectiveStats>
                </TeamSection>
            </TeamsContainer>
        </DetailContainer>
    );
};

export default GameDetailView;
