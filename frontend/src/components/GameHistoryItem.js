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
    cursor: pointer;  // 이 줄 추가

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

    // 아이템 호버 핸들러
    const handleItemHover = (itemId, event) => {
        if (hideTimeoutRef.current) {
            clearTimeout(hideTimeoutRef.current);
            hideTimeoutRef.current = null;
        }

        setHoveredItemId(itemId);
        setMousePosition({ x: event.clientX, y: event.clientY });
        setShowItemModal(true);

        // 스펠 모달이 열려있다면 닫기
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

    // 스펠 호버 핸들러
    const handleSpellHover = (spellId, event) => {
        if (hideTimeoutRef.current) {
            clearTimeout(hideTimeoutRef.current);
            hideTimeoutRef.current = null;
        }

        setHoveredSpellId(spellId);
        setMousePosition({ x: event.clientX, y: event.clientY });
        setShowSpellModal(true);

        // 아이템 모달이 열려있다면 닫기
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

    // 컴포넌트 언마운트 시 타이머 정리
    useEffect(() => {
        return () => {
            if (hideTimeoutRef.current) {
                clearTimeout(hideTimeoutRef.current);
            }
        };
    }, []);

    const handleGameCardClick = async (e) => {
        // 아이템이나 스펠 호버 시에는 게임 상세 내역을 열지 않음
        if (e.target.closest('[data-hover-element]')) {
            console.log('🚫 호버 요소 클릭 - 게임 상세 조회 취소');
            return;
        }

        console.log('🎮 게임 카드 클릭됨!');
        console.log('매치 ID:', game.matchId);
        console.log('현재 확장 상태:', isExpanded);

        if (!isExpanded) {
            console.log('📡 게임 상세 정보 조회 시작...');
            setLoading(true);
            setError(null);

            try {
                console.log('🔍 API 호출 중:', game.matchId);
                const result = await getGameDetail(game.matchId);

                console.log('✅ API 응답 받음:');
                console.log('전체 응답:', result);
                console.log('응답 타입:', typeof result);
                console.log('응답 구조:', Object.keys(result || {}));

                // 이전 대화에서 확인된 바로는 getGameDetail이 직접 데이터를 반환
                // result.success 체크 대신 직접 데이터 사용
                if (result) {
                    console.log('🎯 게임 상세 데이터:');
                    console.log('- 매치 ID:', result.matchId);
                    console.log('- 게임 모드:', result.gameMode);
                    console.log('- 게임 지속시간:', result.gameDuration);
                    console.log('- 게임 날짜:', result.gameDate);

                    if (result.blueTeam) {
                        console.log('🔵 블루팀 정보:');
                        console.log('- 승리 여부:', result.blueTeam.win);
                        console.log('- 플레이어 수:', result.blueTeam.players?.length || 0);
                        console.log('- 팀 스탯:', result.blueTeam.teamStats);

                        if (result.blueTeam.players) {
                            console.log('- 블루팀 플레이어들:');
                            result.blueTeam.players.forEach((player, index) => {
                                console.log(`  ${index + 1}. ${player.riotIdGameName || 'Unknown'} (${player.championName}) - ${player.kills}/${player.deaths}/${player.assists}`);
                            });
                        }
                    }

                    if (result.redTeam) {
                        console.log('🔴 레드팀 정보:');
                        console.log('- 승리 여부:', result.redTeam.win);
                        console.log('- 플레이어 수:', result.redTeam.players?.length || 0);
                        console.log('- 팀 스탯:', result.redTeam.teamStats);

                        if (result.redTeam.players) {
                            console.log('- 레드팀 플레이어들:');
                            result.redTeam.players.forEach((player, index) => {
                                console.log(`  ${index + 1}. ${player.riotIdGameName || 'Unknown'} (${player.championName}) - ${player.kills}/${player.deaths}/${player.assists}`);
                            });
                        }
                    }

                    console.log('📊 전체 게임 상세 데이터 (JSON):');
                    console.log(JSON.stringify(result, null, 2));

                    setGameDetail(result);
                    setIsExpanded(true);
                    console.log('✅ 게임 상세 정보 표시 완료');
                } else {
                    console.error('❌ 응답 데이터가 비어있음');
                    setError('게임 상세 정보를 불러올 수 없습니다.');
                }
            } catch (err) {
                console.error('❌ 게임 상세 조회 실패:');
                console.error('에러 객체:', err);
                console.error('에러 메시지:', err.message);
                console.error('에러 스택:', err.stack);

                if (err.response) {
                    console.error('HTTP 응답 에러:');
                    console.error('- 상태 코드:', err.response.status);
                    console.error('- 응답 데이터:', err.response.data);
                }

                setError(err.message || '네트워크 오류가 발생했습니다.');
            } finally {
                setLoading(false);
                console.log('🏁 게임 상세 조회 프로세스 완료');
            }
        } else {
            console.log('📦 게임 상세 정보 닫기');
            setIsExpanded(false);
            setGameDetail(null);
        }
    };


    return (
        <>
            <GameCard
                win={game.win}
                isExpanded={isExpanded}
                onClick={handleGameCardClick}
            >
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
                <div data-hover-element>
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
                </div>

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
                    {/*<ItemStats>*/}
                    {/*    {itemCount}/6 아이템 · {buildCost.toLocaleString()}G*/}
                    {/*</ItemStats>*/}
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