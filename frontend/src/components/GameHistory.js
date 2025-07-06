import React from 'react';
import styled from 'styled-components';
import GameHistoryItem from './GameHistoryItem';

const GameHistoryContainer = styled.div`
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 8px 24px rgba(0,0,0,0.1);
`;

const GameHistoryTitle = styled.h3`
  font-size: 1.5rem;
  color: #333;
  margin-bottom: 20px;
  text-align: center;
`;

const GameHistory = ({ gameHistory }) => {
    if (!gameHistory || gameHistory.length === 0) {
        return null;
    }

    return (
        <GameHistoryContainer>
            <GameHistoryTitle>최근 {gameHistory.length}경기</GameHistoryTitle>
            {gameHistory.map((game, index) => (
                <GameHistoryItem key={`${game.matchId}-${index}`} game={game} />
            ))}
        </GameHistoryContainer>
    );
};

export default GameHistory;
