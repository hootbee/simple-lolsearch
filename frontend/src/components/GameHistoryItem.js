import React from 'react';
import styled from 'styled-components';

const GameCard = styled.div`
  background: ${props => props.win ? '#e8f5e8' : '#ffeaea'};
  border-left: 4px solid ${props => props.win ? '#4caf50' : '#f44336'};
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  transition: transform 0.2s ease;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0,0,0,0.15);
  }
`;

const GameInfo = styled.div`
  display: flex;
  align-items: center;
  gap: 16px;
`;

const ChampionName = styled.span`
  font-weight: bold;
  font-size: 1.1rem;
  color: #333;
  min-width: 100px;
`;

const KDA = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
`;

const KDANumbers = styled.span`
  font-weight: bold;
  color: #333;
`;

const KDARatio = styled.span`
  font-size: 0.9rem;
  color: #666;
`;

const GameStats = styled.div`
  display: flex;
  gap: 16px;
  align-items: center;
`;

const Stat = styled.div`
  text-align: center;
`;

const StatLabel = styled.div`
  font-size: 0.8rem;
  color: #666;
`;

const StatValue = styled.div`
  font-weight: bold;
  color: #333;
`;

const Result = styled.span`
  font-weight: bold;
  color: ${props => props.win ? '#4caf50' : '#f44336'};
  font-size: 1.1rem;
`;

const GameHistoryItem = ({ game }) => {
    const formatDuration = (seconds) => {
        const minutes = Math.floor(seconds / 60);
        const remainingSeconds = seconds % 60;
        return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`;
    };

    return (
        <GameCard win={game.win}>
            <GameInfo>
                <ChampionName>{game.championName}</ChampionName>
                <KDA>
                    <KDANumbers>{game.kills}/{game.deaths}/{game.assists}</KDANumbers>
                    <KDARatio>{game.kda} KDA</KDARatio>
                </KDA>
            </GameInfo>

            <GameStats>
                <Stat>
                    <StatLabel>CS</StatLabel>
                    <StatValue>{game.cs}</StatValue>
                </Stat>
                <Stat>
                    <StatLabel>골드</StatLabel>
                    <StatValue>{game.goldEarned.toLocaleString()}</StatValue>
                </Stat>
                <Stat>
                    <StatLabel>시야</StatLabel>
                    <StatValue>{game.visionScore}</StatValue>
                </Stat>
                <Stat>
                    <StatLabel>시간</StatLabel>
                    <StatValue>{formatDuration(game.gameDuration)}</StatValue>
                </Stat>
                <Result win={game.win}>{game.win ? '승리' : '패배'}</Result>
            </GameStats>
        </GameCard>
    );
};

export default GameHistoryItem;
