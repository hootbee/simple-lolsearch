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

const FilterContainer = styled.div`
    display: flex;
    flex-direction: column;
    align-items: flex-end;
    gap: 8px;
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

const LoadMoreSection = styled.div`
    display: flex;
    justify-content: center;
    margin-top: 24px;
    padding-top: 20px;
    border-top: 1px solid #eee;
`;

const LoadMoreButton = styled.button`
    padding: 12px 32px;
    background: ${props => props.loading ? '#f8f9fa' : '#007bff'};
    color: ${props => props.loading ? '#666' : 'white'};
    border: none;
    border-radius: 8px;
    font-size: 0.9rem;
    font-weight: 500;
    cursor: ${props => props.loading ? 'not-allowed' : 'pointer'};
    transition: all 0.2s ease;
    min-width: 120px;

    &:hover {
        background: ${props => props.loading ? '#f8f9fa' : '#0056b3'};
        transform: ${props => props.loading ? 'none' : 'translateY(-1px)'};
    }

    &:disabled {
        background: #e9ecef;
        color: #6c757d;
        cursor: not-allowed;
    }
`;

const LoadingSpinner = styled.div`
    display: inline-block;
    width: 16px;
    height: 16px;
    border: 2px solid #f3f3f3;
    border-top: 2px solid #666;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin-right: 8px;

    @keyframes spin {
        0% { transform: rotate(0deg); }
        100% { transform: rotate(360deg); }
    }
`;

const ErrorMessage = styled.div`
    text-align: center;
    color: #dc3545;
    font-size: 0.9rem;
    margin-top: 12px;
    padding: 8px;
    background: #f8d7da;
    border-radius: 6px;
`;

const NoMoreGames = styled.div`
    text-align: center;
    color: #6c757d;
    font-size: 0.9rem;
    padding: 16px;
    background: #f8f9fa;
    border-radius: 8px;
`;

const GameHistory = ({ 
                         gameHistory,
                         onLoadMore,
                         loading = false,
                         hasMore = true,
                         error = null,
                         puuid, // 🔥 gameName, tagLine 대신 puuid 사용
                         onQueueFilterChange, // 🔥 추가: 큐 필터 변경 핸들러
                         selectedQueueId // 🔥 추가: 선택된 큐 ID
                     }) => {
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

    // 🔥 PUUID 기반 더보기 핸들러
    const handleLoadMore = () => {
        if (loading || !hasMore || !onLoadMore || !puuid) return;

        // 마지막 게임의 시간 추출
        const lastGame = gameHistory[gameHistory.length - 1];
        if (lastGame && lastGame.gameCreation) {
            onLoadMore({
                puuid, // 🔥 PUUID 전달
                lastGameTime: lastGame.gameCreation,
                count: 5
            });
        }
    };

    // 더보기 버튼 렌더링 조건
    const shouldShowLoadMore = () => {
        // 필터가 'all'일 때만 더보기 버튼 표시
        if (filter !== 'all') return false;

        // 에러가 있거나 더 이상 게임이 없으면 표시하지 않음
        if (error || !hasMore) return false;

        // PUUID가 없으면 더보기 불가
        if (!puuid) return false;

        // 최소 게임 수가 있어야 더보기 표시
        return gameHistory.length >= 5;
    };

    return (
        <GameHistoryContainer>
            <Header>
                <GameHistoryTitle>
                    최근 {filteredGames.length}/{gameHistory.length}경기
                </GameHistoryTitle>
                <FilterContainer>
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
                    <FilterSection>
                        <FilterButton active={selectedQueueId === null} onClick={() => onQueueFilterChange(null)}>전체</FilterButton>
                        <FilterButton active={selectedQueueId === 420} onClick={() => onQueueFilterChange(420)}>솔로랭크</FilterButton>
                        <FilterButton active={selectedQueueId === 440} onClick={() => onQueueFilterChange(440)}>자유랭크</FilterButton>
                        <FilterButton active={selectedQueueId === 450} onClick={() => onQueueFilterChange(450)}>칼바람 나락</FilterButton>
                    </FilterSection>
                </FilterContainer>
            </Header>

            {filteredGames.map((game, index) => (
                <GameHistoryItem key={`${game.matchId}-${index}`} game={game} />
            ))}

            {/* 더보기 섹션 */}
            {shouldShowLoadMore() && (
                <LoadMoreSection>
                    <LoadMoreButton
                        onClick={handleLoadMore}
                        loading={loading}
                        disabled={loading || !hasMore}
                    >
                        {loading && <LoadingSpinner />}
                        {loading ? '로딩 중...' : '이전 경기 보기'}
                    </LoadMoreButton>
                </LoadMoreSection>
            )}

            {/* 에러 메시지 */}
            {error && filter === 'all' && (
                <LoadMoreSection>
                    <ErrorMessage>
                        {error}
                        <br />
                        <button
                            onClick={handleLoadMore}
                            style={{
                                background: 'none',
                                border: 'none',
                                color: '#007bff',
                                textDecoration: 'underline',
                                cursor: 'pointer',
                                marginTop: '4px'
                            }}
                        >
                            다시 시도
                        </button>
                    </ErrorMessage>
                </LoadMoreSection>
            )}

            {/* 더 이상 게임이 없을 때 */}
            {!hasMore && filter === 'all' && gameHistory.length > 10 && (
                <LoadMoreSection>
                    <NoMoreGames>
                        모든 경기를 불러왔습니다.
                    </NoMoreGames>
                </LoadMoreSection>
            )}
        </GameHistoryContainer>
    );
};

export default GameHistory;
