import React, { useState, useRef, useEffect } from 'react';
import styled from 'styled-components';
import ChampionImage from './ChampionImage';
import ItemBuild from './ItemBuild';
import SpellRuneDisplay from './SpellRuneDisplay';
import ItemDetailModal from './ItemDetailModal';
import SpellDetailModal from './SpellDetailModal';
import { calculateBuildCost, getTotalItems } from '../utils/ItemUtils';
import GameDetailView from "./GameDetailView";
import {getGameDetail} from "../services/api";

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
`;

const GameContent = styled.div`
    display: grid;
    align-items: center;
    flex: 1;
    margin-right: 4px;

    /* 컬럼 간의 간격을 일관되게 조정합니다. */
    column-gap: 12px;

    /* 각 컬럼의 너비를 콘텐츠에 맞게 재조정합니다. */
    grid-template-columns: 220px 100px 150px 225px 180px 120px;

    & > *:not(:last-child) {
        position: relative;
    }

    @media (max-width: 768px) {
        grid-template-columns: 1fr 1fr;
        grid-template-rows: repeat(3, auto);
        column-gap: 15px;
        row-gap: 15px;
        margin-right: 8px;

        & > * { margin-right: 0 !important; }
        & > *::after { display: none; }
    }
`;

const ChampionSection = styled.div`
    display: flex;
    gap: 8px;
    align-items: center;
    padding: 6px 8px;
    border-radius: 6px;
    min-width: 200px; // 최소 너비 설정in
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

// 스펠&룬 영역
const SpellRuneSection = styled.div`
    padding: 6px 8px; // 8px 12px에서 줄임
    border-radius: 6px;
`;


const ItemSection = styled.div`
    text-align: center;
    padding: 6px 8px; // 8px 12px에서 줄임
    border-radius: 6px;
`;

const ItemStats = styled.div`
    font-size: 0.7rem;
    color: #777;
    margin-top: 4px;
`;

const StatsSection = styled.div`
    display: flex;
    gap: 16px;
    padding: 6px 8px;
    border-radius: 6px;
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
    position: relative;
    padding: 6px 8px;
    border-radius: 6px;
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
`;

const GameDuration = styled.div`
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

// 결과 뱃지 영역
const ResultSection = styled.div`
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    gap: 4px;
    padding: 6px 8px; // 8px 12px에서 줄임
    background: ${({ win }) => (win ? 'rgba(76, 175, 80, 0.1)' : 'rgba(244, 67, 54, 0.1)')};
    border-radius: 6px;
    position: relative; /* 또는 absolute. 상황에 따라 */
    top: -5px;
`;

const ResultBadge = styled.div`
    font-weight: bold;
    color: ${({ win }) => (win ? '#4caf50' : '#f44336')};
    font-size: 1.1rem;
    text-align: center;
    
`;

// 🔥 토글 버튼 추가
const ToggleButton = styled.button`
    background: none;
    border: none;
    cursor: pointer;
    padding: 8px;
    border-radius: 4px;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.2s ease;
    color: #666;
    min-width: 32px;
    height: 32px;

    &:hover {
        background: rgba(0, 0, 0, 0.1);
        color: #333;
    }

    &:active {
        transform: scale(0.95);
    }

    &:disabled {
        opacity: 0.5;
        cursor: not-allowed;
    }

    ${({ isLoading }) => isLoading && `
        animation: spin 1s linear infinite;
        
        @keyframes spin {
            from { transform: rotate(0deg); }
            to { transform: rotate(360deg); }
        }
    `}
`;

// 🔥 화살표 아이콘
const ArrowIcon = styled.span`
    font-size: 14px;
    transition: transform 0.2s ease;
    transform: rotate(${({ isExpanded }) => (isExpanded ? '180deg' : '0deg')});
`;

const GameDetailContainer = styled.div`
    background: #f8f9fa;
    border: 1px solid #dee2e6;
    border-top: none;
    border-radius: 0 0 8px 8px;
    margin-bottom: 12px;
    overflow: hidden;
    transition: all 0.3s ease;

    ${({ isExpanded }) => !isExpanded && `
        max-height: 0;
        border: none;
        margin-bottom: 0;
    `}
`;

const LoadingSpinner = styled.div`
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 40px;
    color: #666;
    
    &::after {
        content: '';
        width: 20px;
        height: 20px;
        border: 2px solid #f3f3f3;
        border-top: 2px solid #007bff;
        border-radius: 50%;
        animation: spin 1s linear infinite;
    }
    
    @keyframes spin {
        0% { transform: rotate(0deg); }
        100% { transform: rotate(360deg); }
    }
`;

const ErrorMessage = styled.div`
    padding: 20px;
    text-align: center;
    color: #dc3545;
    background: #f8d7da;
    border: 1px solid #f5c6cb;
    margin: 10px;
    border-radius: 4px;
`;



/* ---------- Component ---------- */
const GameHistoryItem = ({ game }) => {
    const [showTooltip, setShowTooltip] = useState(false);

    // 아이템 호버 상태
    const [hoveredItemId, setHoveredItemId] = useState(null);
    const [showItemModal, setShowItemModal] = useState(false);

    // 스펠 호버 상태
    const [hoveredSpellId, setHoveredSpellId] = useState(null);
    const [showSpellModal, setShowSpellModal] = useState(false);

    const [mousePosition, setMousePosition] = useState({ x: 0, y: 0 });
    const hideTimeoutRef = useRef(null);

    const [isExpanded, setIsExpanded] = useState(false);
    const [gameDetail, setGameDetail] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    // 기존 helper 함수들...
    const formatDuration = (seconds) => {
        const minutes = Math.floor(seconds / 60);
        const remainingSeconds = seconds % 60;
        return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`;
    };

    const getRelativeTime = () => {
        return game.relativeTime || '시간 정보 없음';
    };

    const getDetailedTime = () => {
        return game.detailedTime || '';
    };

    // 아이템 관련 계산
    const itemCount = getTotalItems(game.items || []);
    const buildCost = calculateBuildCost(game.items || []);

    // 기존 호버 핸들러들...
    const handleItemHover = (itemId, event) => {
        if (hideTimeoutRef.current) {
            clearTimeout(hideTimeoutRef.current);
            hideTimeoutRef.current = null;
        }

        setHoveredItemId(itemId);
        setMousePosition({ x: event.clientX, y: event.clientY });
        setShowItemModal(true);

        setShowSpellModal(false);
        setHoveredSpellId(null);
    };

    const handleItemHoverEnd = () => {
        if (hideTimeoutRef.current) {
            clearTimeout(hideTimeoutRef.current);
        }

        hideTimeoutRef.current = setTimeout(() => {
            setShowItemModal(false);
            setHoveredItemId(null);
            hideTimeoutRef.current = null;
        }, 100);
    };

    const handleSpellHover = (spellId, event) => {
        if (hideTimeoutRef.current) {
            clearTimeout(hideTimeoutRef.current);
            hideTimeoutRef.current = null;
        }

        setHoveredSpellId(spellId);
        setMousePosition({ x: event.clientX, y: event.clientY });
        setShowSpellModal(true);

        setShowItemModal(false);
        setHoveredItemId(null);
    };

    const handleSpellHoverEnd = () => {
        if (hideTimeoutRef.current) {
            clearTimeout(hideTimeoutRef.current);
        }

        hideTimeoutRef.current = setTimeout(() => {
            setShowSpellModal(false);
            setHoveredSpellId(null);
            hideTimeoutRef.current = null;
        }, 100);
    };

    const handleModalClose = () => {
        if (hideTimeoutRef.current) {
            clearTimeout(hideTimeoutRef.current);
            hideTimeoutRef.current = null;
        }

        setShowItemModal(false);
        setHoveredItemId(null);
        setShowSpellModal(false);
        setHoveredSpellId(null);
    };

    useEffect(() => {
        return () => {
            if (hideTimeoutRef.current) {
                clearTimeout(hideTimeoutRef.current);
            }
        };
    }, []);

    // 🔥 토글 버튼만 클릭 시 실행되는 함수
    const handleToggleClick = async (e) => {
        e.stopPropagation(); // 이벤트 버블링 방지

        if (loading) {
            console.log('🚫 이미 로딩 중 - 클릭 무시');
            return;
        }

        console.log('🔽 토글 버튼 클릭됨!');
        console.log('매치 ID:', game.matchId);
        console.log('현재 확장 상태:', isExpanded);

        if (!isExpanded) {
            console.log('📡 게임 상세 정보 조회 시작...');
            setLoading(true);
            setError(null);

            try {
                console.log('🔍 API 호출 중:', game.matchId);
                const result = await getGameDetail(game.matchId);

                if (result) {
                    console.log('✅ 게임 상세 데이터 수신 완료');
                    setGameDetail(result);
                    setIsExpanded(true);
                } else {
                    console.error('❌ 응답 데이터가 비어있음');
                    setError('게임 상세 정보를 불러올 수 없습니다.');
                }
            } catch (err) {
                console.error('❌ 게임 상세 조회 실패:', err);
                setError(err.message || '네트워크 오류가 발생했습니다.');
            } finally {
                setLoading(false);
            }
        } else {
            console.log('📦 게임 상세 정보 닫기');
            setIsExpanded(false);
            setGameDetail(null);
        }
    };

    return (
        <>
            <GameCard win={game.win} isExpanded={isExpanded}>
                <GameContent>
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

                    {/* 게임 결과 */}
                    <ResultSection win={game.win}>
                        <ResultBadge win={game.win}>
                            {game.win ? '승리' : '패배'}
                        </ResultBadge>
                        <GameMode>{game.gameMode}</GameMode>
                    </ResultSection>

                    {/* 스펠 & 룬 */}
                    <SpellRuneSection data-hover-element>
                        <SpellRuneDisplay
                            summonerSpell1Id={game.summonerSpell1Id}
                            summonerSpell2Id={game.summonerSpell2Id}
                            keystoneId={game.keystoneId}
                            primaryRuneTree={game.primaryRuneTree}
                            secondaryRuneTree={game.secondaryRuneTree}
                            statRunes={game.statRunes}
                            onSpellHover={handleSpellHover}
                            onSpellHoverEnd={handleSpellHoverEnd}
                        />
                    </SpellRuneSection>

                    {/* 아이템 빌드 */}
                    <ItemSection>
                        <div data-hover-element>
                            <ItemBuild
                                items={game.items || []}
                                trinket={game.trinket || 0}
                                size={26}
                                onItemHover={handleItemHover}
                                onItemHoverEnd={handleItemHoverEnd}
                            />
                        </div>
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
                        <GameDuration>{formatDuration(game.gameDuration)}</GameDuration>
                    </TimeSection>
                </GameContent>

                {/* 🔥 토글 버튼 */}
                <ToggleButton
                    onClick={handleToggleClick}
                    disabled={loading}
                    isLoading={loading}
                    title={isExpanded ? '상세 정보 닫기' : '상세 정보 보기'}
                >
                    {loading ? (
                        <span>⏳</span>
                    ) : (
                        <ArrowIcon isExpanded={isExpanded}>▼</ArrowIcon>
                    )}
                </ToggleButton>
            </GameCard>

            {/* 게임 상세 내역 컨테이너 */}
            <GameDetailContainer isExpanded={isExpanded}>
                {loading && <LoadingSpinner />}
                {error && <ErrorMessage>{error}</ErrorMessage>}
                {gameDetail && <GameDetailView gameDetail={gameDetail} />}
            </GameDetailContainer>

            {/* 기존 모달들 */}
            <ItemDetailModal
                itemId={hoveredItemId}
                isVisible={showItemModal}
                mousePosition={mousePosition}
                onClose={handleModalClose}
            />

            <SpellDetailModal
                spellId={hoveredSpellId}
                isVisible={showSpellModal}
                mousePosition={mousePosition}
                onClose={handleModalClose}
            />
        </>
    );
};

export default GameHistoryItem;
