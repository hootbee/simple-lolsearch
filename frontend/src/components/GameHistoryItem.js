import React, {useRef, useState} from 'react';
import styled from 'styled-components';
import ChampionImage from './ChampionImage';
import ItemBuild from './ItemBuild';
import SpellRuneDisplay from './SpellRuneDisplay';
import { calculateBuildCost, getTotalItems } from '../utils/ItemUtils';
import ItemDetailModal from './ItemDetailModal';
import {useEffect} from "react";

/* ---------- Styled Components ---------- */
const GameCard = styled.div`
    background: ${({ win }) => (win ? '#e8f5e8' : '#ffeaea')};
    border-left: 4px solid ${({ win }) => (win ? '#4caf50' : '#f44336')};
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

const ChampionSection = styled.div`
    display: flex;
    gap: 16px;
    align-items: center;
`;

const ChampionInfo = styled.div`
    display: flex;
    flex-direction: column;
`;

const ChampionName = styled.div`
    font-weight: bold;
    font-size: 1.1rem;
    color: #333;
`;

const KDAInfo = styled.div`
    font-size: 0.9rem;
    color: #666;
`;

const ItemSection = styled.div`
    min-width: 200px;
    text-align: center;
`;

const ItemStats = styled.div`
    font-size: 0.7rem;
    color: #777;
    margin-top: 4px;
`;

const StatsSection = styled.div`
    display: flex;
    gap: 12px;
`;

const StatItem = styled.div`
    text-align: center;
    
    b {
        display: block;
        font-size: 0.8rem;
        color: #666;
    }
    
    span {
        font-weight: bold;
        color: #333;
        font-size: 0.9rem;
    }
`;

const TimeSection = styled.div`
    text-align: center;
    min-width: 90px;
    position: relative;
`;

const RelativeTime = styled.div`
    font-weight: bold;
    cursor: help;
    font-size: 0.95rem;
    color: #007bff;
    background: rgba(0, 123, 255, 0.1);
    padding: 6px 10px;
    border-radius: 8px;
    transition: all 0.2s ease;

    &:hover {
        background: rgba(0, 123, 255, 0.2);
        transform: scale(1.05);
    }
`;

const GameMode = styled.div`
    font-size: 0.75rem;
    color: #666;
    margin-top: 4px;
`;

const Tooltip = styled.div`
    position: absolute;
    bottom: 120%;
    left: 50%;
    transform: translateX(-50%);
    background: rgba(0, 0, 0, 0.85);
    color: #fff;
    padding: 6px 10px;
    border-radius: 6px;
    font-size: 0.75rem;
    white-space: nowrap;
    z-index: 1000;
    
    &::after {
        content: '';
        position: absolute;
        top: 100%;
        left: 50%;
        transform: translateX(-50%);
        border: 5px solid transparent;
        border-top-color: rgba(0, 0, 0, 0.85);
    }
`;

const ResultBadge = styled.div`
    font-weight: bold;
    color: ${({ win }) => (win ? '#4caf50' : '#f44336')};
    font-size: 1.1rem;
`;

const GameHistoryItem = ({ game }) => {
    const [showTooltip, setShowTooltip] = useState(false);
    // 아이템 호버 상태 추가
    const [hoveredItemId, setHoveredItemId] = useState(null);
    const [showItemModal, setShowItemModal] = useState(false);
    const [mousePosition, setMousePosition] = useState({ x: 0, y: 0 }); // 마우스 위치 추가

    // 타이머 관리를 위한 ref 추가
    const hideTimeoutRef = useRef(null);

    // 게임 시간 포맷팅
    const formatDuration = (seconds) => {
        const minutes = Math.floor(seconds / 60);
        const remainingSeconds = seconds % 60;
        return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`;
    };

    // 상대적 시간 계산
    const getRelativeTime = () => {
        return game.relativeTime || '시간 정보 없음';
    };

    // 상세 시간 정보
    const getDetailedTime = () => {
        return game.detailedTime || '';
    };

    // 아이템 관련 계산
    const itemCount = getTotalItems(game.items || []);
    const buildCost = calculateBuildCost(game.items || []);

    // 아이템 호버 핸들러 - 타이머 취소 로직 추가
    const handleItemHover = (itemId, event) => {
        // 기존 타이머가 있다면 취소
        if (hideTimeoutRef.current) {
            clearTimeout(hideTimeoutRef.current);
            hideTimeoutRef.current = null;
        }

        setHoveredItemId(itemId);
        setMousePosition({ x: event.clientX, y: event.clientY });
        setShowItemModal(true);
    };

    const handleItemHoverEnd = () => {
        // 기존 타이머가 있다면 취소
        if (hideTimeoutRef.current) {
            clearTimeout(hideTimeoutRef.current);
        }

        // 새로운 타이머 설정
        hideTimeoutRef.current = setTimeout(() => {
            setShowItemModal(false);
            setHoveredItemId(null);
            hideTimeoutRef.current = null;
        }, 100);
    };

    const handleModalClose = () => {
        // 타이머 취소
        if (hideTimeoutRef.current) {
            clearTimeout(hideTimeoutRef.current);
            hideTimeoutRef.current = null;
        }

        setShowItemModal(false);
        setHoveredItemId(null);
    };

    // 컴포넌트 언마운트 시 타이머 정리
    useEffect(() => {
        return () => {
            if (hideTimeoutRef.current) {
                clearTimeout(hideTimeoutRef.current);
            }
        };
    }, []);

    return (
        <>
            <GameCard win={game.win}>
                {/* 챔피언 정보 */}
                <ChampionSection>
                    <ChampionImage championName={game.championName} size="48px" />
                    <ChampionInfo>
                        <ChampionName>{game.championName}</ChampionName>
                        <KDAInfo>
                            {game.kills}/{game.deaths}/{game.assists} · {game.kda} KDA
                        </KDAInfo>
                    </ChampionInfo>
                </ChampionSection>

                {/* 스펠 & 룬 */}
                <SpellRuneDisplay
                    summonerSpell1Id={game.summonerSpell1Id}
                    summonerSpell2Id={game.summonerSpell2Id}
                    keystoneId={game.keystoneId}
                    primaryRuneTree={game.primaryRuneTree}
                    secondaryRuneTree={game.secondaryRuneTree}
                    statRunes={game.statRunes}
                />

                {/* 아이템 빌드 - 호버 핸들러 추가 */}
                <ItemSection>
                    <ItemBuild
                        items={game.items || []}
                        trinket={game.trinket || 0}
                        size={26}
                        onItemHover={handleItemHover}
                        onItemHoverEnd={handleItemHoverEnd}
                    />
                    <ItemStats>
                        {itemCount}/6 아이템 · {buildCost.toLocaleString()}G
                    </ItemStats>
                </ItemSection>

                {/* 게임 스탯 */}
                <StatsSection>
                    <StatItem>
                        <b>CS</b>
                        <span>{game.cs}</span>
                    </StatItem>
                    <StatItem>
                        <b>골드</b>
                        <span>{game.goldEarned?.toLocaleString()}</span>
                    </StatItem>
                    <StatItem>
                        <b>시야</b>
                        <span>{game.visionScore}</span>
                    </StatItem>
                    <StatItem>
                        <b>시간</b>
                        <span>{formatDuration(game.gameDuration)}</span>
                    </StatItem>
                </StatsSection>

                {/* 시간 정보 */}
                <TimeSection>
                    <RelativeTime
                        onMouseEnter={() => setShowTooltip(true)}
                        onMouseLeave={() => setShowTooltip(false)}
                    >
                        {getRelativeTime()}
                    </RelativeTime>

                    {showTooltip && (
                        <Tooltip>
                            {getDetailedTime()}
                        </Tooltip>
                    )}

                    <GameMode>{game.gameMode}</GameMode>
                </TimeSection>

                {/* 게임 결과 */}
                <ResultBadge win={game.win}>
                    {game.win ? '승리' : '패배'}
                </ResultBadge>
            </GameCard>

            {/* 아이템 상세 모달 - 마우스 위치 전달 */}
            <ItemDetailModal
                itemId={hoveredItemId}
                isVisible={showItemModal}
                mousePosition={mousePosition}
                onClose={handleModalClose}
            />
        </>
    );
};

export default GameHistoryItem;