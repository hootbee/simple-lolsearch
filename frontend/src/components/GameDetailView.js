import React from 'react';
import styled from 'styled-components';
import { useNavigate } from 'react-router-dom';
import ChampionImage from './ChampionImage';
import ItemBuild from './ItemBuild';
import { getAccountByPuuid } from '../services/api';

const getRankBackgroundColor = (tier) => {
    switch (tier ? tier.toUpperCase() : '') {
        case 'IRON': return '#4a4a4a';
        case 'BRONZE': return '#8b4513';
        case 'SILVER': return '#c0c0c0';
        case 'GOLD': return '#ffd700';
        case 'PLATINUM': return '#73A9AD';
        case 'EMERALD': return '#50c878';
        case 'DIAMOND': return '#b9f2ff';
        case 'MASTER': return '#9932cc';
        case 'GRANDMASTER': return '#ff4500';
        case 'CHALLENGER': return '#00bfff';
        default: return '#808080'; // UNRANKED 또는 기타
    }
};

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
    max-width:520px;
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
    /* 챔피언 | 닉네임 | KDA | 아이템 | CS/골드 | 랭크 */
    grid-template-columns: 35px 105px 65px 105px 50px 45px;
    align-items: center;
    gap: 8px; /* 간격 조금 더 벌림 */
    padding: 8px;
    background: white;
    border-radius: 4px;
    font-size: 0.8rem;
    
    &:hover {
        background: #f8f9fa;
    }
    
    @media (max-width: 768px) {
        grid-template-columns: 30px 1fr 60px 80px;
        gap: 5px;
        font-size: 0.75rem;
    }
`;

const ChampionIconContainer = styled.div`
    position: relative;
    width: 32px;
    height: 32px;
`;

const ChampionLevel = styled.div`
    position: absolute;
    bottom: 0;
    right: 0;
    background-color: rgba(0, 0, 0, 0.8);
    color: white;
    font-size: 11px;
    padding: 1px 3px;
    border-radius: 3px;
    font-weight: bold;
`;

// 🔥 클릭 가능한 플레이어 이름 스타일 추가
const PlayerName = styled.div`
    font-weight: bold;
    color: #0066cc;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    cursor: pointer;
    transition: all 0.2s ease;
    font-size: 0.75rem; /* 닉네임 크기 더 줄임 */

    &:hover {
        color: #004499;
        text-decoration: underline;
        background: #f0f7ff;
        padding: 2px 4px;
        border-radius: 4px;
    }
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
    font-size: 0.75rem;
    color: #fff;
    font-weight: bold;
    padding: 2px 3px; /* 좌우 패딩 줄임 */
    border-radius: 4px;
    background-color: ${({ tier }) => getRankBackgroundColor(tier)};
`;

const PlayerStats = styled.div`
    text-align: center;
    display: flex;
    flex-direction: column;
    font-size: 0.7rem;
    color: #666;
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
    const navigate = useNavigate();

    const formatDuration = (seconds) => {
        const minutes = Math.floor(seconds / 60);
        const remainingSeconds = seconds % 60;
        return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`;
    };

        // 🔥 랭크 축약 함수 추가
    const formatRank = (tier, rank) => {
        if (!tier || tier === 'UNRANKED') return 'UR';

        const tierMap = {
            'IRON': 'I',
            'BRONZE': 'B',
            'SILVER': 'S',
            'GOLD': 'G',
            'PLATINUM': 'P',
            'EMERALD': 'E',
            'DIAMOND': 'D',
            'MASTER': 'M',
            'GRANDMASTER': 'GM',
            'CHALLENGER': 'C'
        };

        const rankMap = {
            'I': '1',
            'II': '2',
            'III': '3',
            'IV': '4',
            'V': '5'
        };

        const shortTier = tierMap[tier.toUpperCase()] || tier.charAt(0);

        // 마스터 이상은 앞에 1 붙이기
        if (['MASTER', 'GRANDMASTER', 'CHALLENGER'].includes(tier.toUpperCase())) {
            return `${shortTier}1`;
        }

        // 일반 티어는 숫자 포함
        const shortRank = rankMap[rank] || rank;
        return shortRank ? `${shortTier}${shortRank}` : shortTier;
    };



        // 🔥 플레이어 클릭 핸들러
        const handlePlayerClick = async (player) => {
            try {
                console.log('🔍 플레이어 클릭:', player);

                // PUUID가 있으면 해당 PUUID로 계정 정보 조회
                if (player.puuid) {
                    console.log('PUUID로 계정 정보 조회:', player.puuid);
                    const accountData = await getAccountByPuuid(player.puuid);

                    if (accountData && accountData.gameName && accountData.tagLine) {
                        const encodedGameName = encodeURIComponent(accountData.gameName);
                        const encodedTagLine = encodeURIComponent(accountData.tagLine);

                        console.log('검색 페이지로 이동:', `${encodedGameName}#${encodedTagLine}`);
                        navigate(`/search/${encodedGameName}/${encodedTagLine}`);
                    } else {
                        console.warn('계정 정보를 찾을 수 없습니다');
                        alert('해당 플레이어의 계정 정보를 찾을 수 없습니다.');
                    }
                } else if (player.riotIdGameName && player.riotIdTagLine) {
                    // 백업: riotId 정보가 있으면 사용
                    const encodedGameName = encodeURIComponent(player.riotIdGameName);
                    const encodedTagLine = encodeURIComponent(player.riotIdTagLine);

                    console.log('backup: riotId로 검색 페이지 이동:', `${encodedGameName}#${encodedTagLine}`);
                    navigate(`/search/${encodedGameName}/${encodedTagLine}`);
                } else {
                    console.warn('플레이어 정보가 부족합니다:', player);
                    alert('해당 플레이어의 정보가 부족합니다.');
                }
            } catch (error) {
                console.error('플레이어 정보 조회 실패:', error);
                alert('플레이어 정보를 불러오는 중 오류가 발생했습니다.');
            }
        };

        // 🔥 플레이어 행 컴포넌트
        const PlayerRowComponent = ({player, index}) => {
            console.log('Player champLevel:', player.champLevel);
            return (
            <PlayerRow key={index}>
                <ChampionIconContainer>
                    <ChampionImage
                        championName={player.championName}
                        size="32px"
                    />
                    <ChampionLevel>{player.champLevel}</ChampionLevel>
                </ChampionIconContainer>
                <div>
                    <PlayerName onClick={() => handlePlayerClick(player)}>
                        {player.riotIdGameName || 'Unknown'}
                    </PlayerName>
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
                <PlayerStats>
                    <div>CS: {player.cs}</div>
                    <div>골드: {player.goldEarned?.toLocaleString()}</div>
                </PlayerStats>
                <PlayerRank
                    tier={player.tier}
                >
                    {formatRank(player.tier, player.rank)}
                </PlayerRank>
            </PlayerRow>
        )};

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
                                <PlayerRowComponent key={index} player={player} index={index}/>
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
                                <PlayerRowComponent key={index} player={player} index={index}/>
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
