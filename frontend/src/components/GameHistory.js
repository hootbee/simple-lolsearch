import React, { useState } from 'react';
import styled from 'styled-components';
import GameHistoryItem from './GameHistoryItem';

const GameHistoryContainer = styled.div`
    background: white;
    border-radius: 12px;
    padding: 24px;
    box-shadow: 0 8px 24px rgba(0,0,0,0.1);
    margin-top: 20px;
`;

const Header = styled.div`
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
`;

const GameHistoryTitle = styled.h3`
    font-size: 1.5rem;
    color: #333;
    margin: 0;
`;

const FilterSection = styled.div`
    display: flex;
    gap: 8px;
`;

const FilterButton = styled.button`
    padding: 6px 12px;
    border: 1px solid ${props => props.active ? '#007bff' : '#ddd'};
    background: ${props => props.active ? '#007bff' : 'white'};
    color: ${props => props.active ? 'white' : '#666'};
    border-radius: 6px;
    font-size: 0.8rem;
    cursor: pointer;
    transition: all 0.2s ease;

    &:hover {
        background: ${props => props.active ? '#0056b3' : '#f8f9fa'};
    }
`;

const GameHistory = ({ gameHistory }) => {
    const [filter, setFilter] = useState('all');

    if (!gameHistory || gameHistory.length === 0) {
        return null;
    }

    // 시간 기반 필터링
    const filterGames = (games, filter) => {
        switch (filter) {
            case 'today':
                return games.filter(game =>
                    game.gameDate?.includes('시간 전') ||
                    game.gameDate?.includes('분 전')
                );
            case 'week':
                return games.filter(game => {
                    if (!game.gameCreation) return false;
                    const gameDate = new Date(game.gameCreation);
                    const weekAgo = new Date(Date.now() - 7 * 24 * 60 * 60 * 1000);
                    return gameDate > weekAgo;
                });
            case 'month':
                return games.filter(game => {
                    if (!game.gameCreation) return false;
                    const gameDate = new Date(game.gameCreation);
                    const monthAgo = new Date(Date.now() - 30 * 24 * 60 * 60 * 1000);
                    return gameDate > monthAgo;
                });
            default:
                return games;
        }
    };

    const filteredGames = filterGames(gameHistory, filter);

    return (
        <GameHistoryContainer>
            <Header>
                <GameHistoryTitle>
                    최근 {filteredGames.length}/{gameHistory.length}경기
                </GameHistoryTitle>
                <FilterSection>
                    <FilterButton
                        active={filter === 'all'}
                        onClick={() => setFilter('all')}
                    >
                        전체
                    </FilterButton>
                    <FilterButton
                        active={filter === 'today'}
                        onClick={() => setFilter('today')}
                    >
                        오늘
                    </FilterButton>
                    <FilterButton
                        active={filter === 'week'}
                        onClick={() => setFilter('week')}
                    >
                        최근 7일
                    </FilterButton>
                    <FilterButton
                        active={filter === 'month'}
                        onClick={() => setFilter('month')}
                    >
                        최근 30일
                    </FilterButton>
                </FilterSection>
            </Header>

            {filteredGames.map((game, index) => (
                <GameHistoryItem key={`${game.matchId}-${index}`} game={game} />
            ))}
        </GameHistoryContainer>
    );
};

export default GameHistory;
