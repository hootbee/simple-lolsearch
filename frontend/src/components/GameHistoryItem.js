import React, { useState } from 'react';
import styled from 'styled-components';
import ChampionImage from './ChampionImage';
import ItemBuild from './ItemBuild';
import {
    getItemName,
    getItemCategory,
    getItemBorderColor,
    calculateBuildCost,
    getTotalItems
} from '../utils/ItemUtils';

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
    flex: 1;
`;

const ChampionInfo = styled.div`
    display: flex;
    align-items: center;
    gap: 12px;
    min-width: 120px;
`;

const ChampionName = styled.span`
    font-weight: bold;
    font-size: 1.1rem;
    color: #333;
`;

const KDA = styled.div`
    display: flex;
    flex-direction: column;
    align-items: center;
    min-width: 80px;
`;

const KDANumbers = styled.span`
    font-weight: bold;
    color: #333;
    font-size: 1rem;
`;

const KDARatio = styled.span`
    font-size: 0.8rem;
    color: #666;
`;

const ItemSection = styled.div`
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 4px;
    min-width: 200px;
`;

const ItemBuildContainer = styled.div`
    display: flex;
    align-items: center;
    gap: 4px;
`;

const ItemInfo = styled.div`
    display: flex;
    gap: 8px;
    font-size: 0.7rem;
    color: #666;
`;

const ItemCount = styled.span`
    color: #007bff;
    font-weight: 500;
`;

const BuildCost = styled.span`
    color: #f39c12;
    font-weight: 500;
`;

const GameStats = styled.div`
    display: flex;
    gap: 16px;
    align-items: center;
`;

const Stat = styled.div`
    text-align: center;
    min-width: 50px;
`;

const StatLabel = styled.div`
    font-size: 0.8rem;
    color: #666;
`;

const StatValue = styled.div`
    font-weight: bold;
    color: #333;
    font-size: 0.9rem;
`;

const Result = styled.span`
    font-weight: bold;
    color: ${props => props.win ? '#4caf50' : '#f44336'};
    font-size: 1.1rem;
    min-width: 50px;
    text-align: center;
`;

const TimeInfo = styled.div`
    display: flex;
    flex-direction: column;
    align-items: center;
    min-width: 90px;
    gap: 4px;
`;

const RelativeTime = styled.div`
    font-size: 0.95rem;
    font-weight: bold;
    color: #007bff;
    background: rgba(0, 123, 255, 0.1);
    padding: 6px 10px;
    border-radius: 8px;
    cursor: help;
    transition: all 0.2s ease;
    text-align: center;
    
    &:hover {
        background: rgba(0, 123, 255, 0.2);
        transform: scale(1.05);
    }
`;

const GameMode = styled.div`
    font-size: 0.75rem;
    color: #666;
    text-align: center;
    background: rgba(0, 0, 0, 0.05);
    padding: 2px 6px;
    border-radius: 4px;
`;

const Tooltip = styled.div`
    position: absolute;
    bottom: 100%;
    left: 50%;
    transform: translateX(-50%);
    background: rgba(0, 0, 0, 0.9);
    color: white;
    padding: 8px 12px;
    border-radius: 6px;
    font-size: 0.8rem;
    white-space: nowrap;
    z-index: 1000;
    margin-bottom: 5px;
    opacity: ${props => props.show ? 1 : 0};
    visibility: ${props => props.show ? 'visible' : 'hidden'};
    transition: all 0.2s ease;

    &::after {
        content: '';
        position: absolute;
        top: 100%;
        left: 50%;
        transform: translateX(-50%);
        border: 5px solid transparent;
        border-top-color: rgba(0, 0, 0, 0.9);
    }
`;

const GameHistoryItem = ({ game }) => {
    const [showTooltip, setShowTooltip] = useState(false);

    const formatDuration = (seconds) => {
        const minutes = Math.floor(seconds / 60);
        const remainingSeconds = seconds % 60;
        return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`;
    };

    const calculateTimeAgo = (timestamp) => {
        if (!timestamp) return '시간 정보 없음';

        const now = new Date();
        const gameTime = new Date(timestamp);
        const diffMs = now - gameTime;

        const diffMinutes = Math.floor(diffMs / (1000 * 60));
        const diffHours = Math.floor(diffMs / (1000 * 60 * 60));
        const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));

        if (diffMinutes < 60) {
            return `${diffMinutes}분 전`;
        } else if (diffHours < 24) {
            return `${diffHours}시간 전`;
        } else if (diffDays <= 30) {
            return `${diffDays}일 전`;
        } else {
            return "30일 이전";
        }
    };

    const getRelativeTime = () => {
        if (game.relativeTime) {
            return game.relativeTime;
        }

        if (game.gameDate && (game.gameDate.includes('월') && game.gameDate.includes('일'))) {
            return "30일 이전";
        }

        if (game.gameDate && !game.gameDate.includes('-')) {
            return game.gameDate;
        }

        if (game.gameCreation) {
            return calculateTimeAgo(game.gameCreation);
        }

        return '시간 정보 없음';
    };

    const getDetailedTime = () => {
        if (game.detailedTime) {
            return game.detailedTime;
        }

        if (game.gameCreation) {
            const date = new Date(game.gameCreation);
            return date.toLocaleString('ko-KR', {
                year: 'numeric',
                month: 'long',
                day: 'numeric',
                hour: '2-digit',
                minute: '2-digit',
                timeZone: 'Asia/Seoul'
            });
        }

        return '상세 시간 정보 없음';
    };

    // 아이템 관련 계산
    const itemCount = getTotalItems(game.items || []);
    const buildCost = calculateBuildCost(game.items || []);

    return (
        <GameCard win={game.win}>
            <GameInfo>
                <ChampionInfo>
                    <ChampionImage
                        championName={game.championName}
                        size="48px"
                    />
                    <ChampionName>{game.championName}</ChampionName>
                </ChampionInfo>

                <KDA>
                    <KDANumbers>{game.kills}/{game.deaths}/{game.assists}</KDANumbers>
                    <KDARatio>{game.kda} KDA</KDARatio>
                </KDA>

                <ItemSection>
                    <ItemBuildContainer>
                        <ItemBuild
                            items={game.items || []}
                            trinket={game.trinket || 0}
                            size={28}
                        />
                    </ItemBuildContainer>
                    <ItemInfo>
                        <ItemCount>{itemCount}/6 아이템</ItemCount>
                        <BuildCost>{buildCost.toLocaleString()}G</BuildCost>
                    </ItemInfo>
                </ItemSection>
            </GameInfo>

            <GameStats>
                <Stat>
                    <StatLabel>CS</StatLabel>
                    <StatValue>{game.cs}</StatValue>
                </Stat>
                <Stat>
                    <StatLabel>골드</StatLabel>
                    <StatValue>{game.goldEarned?.toLocaleString()}</StatValue>
                </Stat>
                <Stat>
                    <StatLabel>시야</StatLabel>
                    <StatValue>{game.visionScore}</StatValue>
                </Stat>
                <Stat>
                    <StatLabel>시간</StatLabel>
                    <StatValue>{formatDuration(game.gameDuration)}</StatValue>
                </Stat>

                <TimeInfo>
                    <RelativeTime
                        onMouseEnter={() => setShowTooltip(true)}
                        onMouseLeave={() => setShowTooltip(false)}
                    >
                        {getRelativeTime()}
                    </RelativeTime>

                    <Tooltip show={showTooltip}>
                        {getDetailedTime()}
                    </Tooltip>

                    <GameMode>{game.gameMode}</GameMode>
                </TimeInfo>

                <Result win={game.win}>{game.win ? '승리' : '패배'}</Result>
            </GameStats>
        </GameCard>
    );
};

export default GameHistoryItem;
