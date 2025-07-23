import React, { useState } from 'react';
import styled from 'styled-components';
import {
    getTierImageUrl,
    getAlternativeTierUrls,
    getTierColor,
    getTierKoreanName,
    getRankKoreanName
} from '../utils/tierUtils';

const RankContainer = styled.div`
    background: white;
    border-radius: 12px;
    padding: 24px;
    margin-bottom: 30px;
    box-shadow: 0 8px 24px rgba(0,0,0,0.1);
`;

const RankTitle = styled.h3`
    font-size: 1.5rem;
    color: #333;
    margin-bottom: 20px;
    text-align: center;
`;

const RankItem = styled.div`
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px;
    margin-bottom: 12px;
    background: ${props => props.queueType === 'RANKED_SOLO_5x5' ? '#f8f9ff' : '#fff8f0'};
    border-left: 4px solid ${props => props.queueType === 'RANKED_SOLO_5x5' ? '#4f46e5' : '#f59e0b'};
    border-radius: 8px;
`;

const LeftSection = styled.div`
    display: flex;
    align-items: center;
    gap: 16px;
`;

const QueueType = styled.div`
    font-weight: bold;
    color: #333;
    font-size: 1.1rem;
    min-width: 80px;
`;

const TierImageContainer = styled.div`
    display: flex;
    align-items: center;
    gap: 12px;
`;

const TierImage = styled.img`
    width: 64px;
    height: 64px;
    object-fit: contain;
    filter: drop-shadow(0 2px 4px rgba(0,0,0,0.3));
`;

const TierInfo = styled.div`
    text-align: left;
`;

const TierDisplay = styled.div`
    font-size: 1.2rem;
    font-weight: bold;
    color: ${props => props.tierColor};
    margin-bottom: 4px;
`;

const LPDisplay = styled.div`
    font-size: 0.9rem;
    color: #666;
`;

const WinRate = styled.div`
    text-align: right;
`;

const WinRatePercent = styled.div`
    font-size: 1.1rem;
    font-weight: bold;
    color: ${props => props.rate >= 60 ? '#10b981' : props.rate >= 50 ? '#f59e0b' : '#ef4444'};
`;

const WinLoss = styled.div`
    font-size: 0.9rem;
    color: #666;
`;

const UnrankedMessage = styled.div`
    text-align: center;
    color: #666;
    font-size: 1.1rem;
    padding: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 12px;
`;

const UnrankedImage = styled.img`
    width: 48px;
    height: 48px;
    object-fit: contain;
    opacity: 0.6;
`;

const TierTextFallback = styled.div`
  position: absolute;
  top: 0;
  left: 0;
  width: 64px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: ${props => props.tierColor}20;
  border: 3px solid ${props => props.tierColor};
  border-radius: 12px;
  font-weight: bold;
  font-size: 1.2rem;
  color: ${props => props.tierColor};
  text-align: center;
`;

const RankInfo = ({ leagueEntries }) => {
    const [imageRetryIndex, setImageRetryIndex] = useState({});

    const getQueueTypeName = (queueType) => {
        switch (queueType) {
            case 'RANKED_SOLO_5x5':
                return '솔로랭크';
            case 'RANKED_FLEX_SR':
                return '자유랭크';
            default:
                return queueType;
        }
    };

    const calculateWinRate = (wins, losses) => {
        const total = wins + losses;
        return total > 0 ? Math.round((wins / total) * 100) : 0;
    };

    const handleImageError = (index, tier, rank, e) => {
        console.error(`이미지 로드 실패: ${tier} ${rank}`);
        console.error('Failed URL:', e.target.src);

        const currentRetryIndex = imageRetryIndex[index] || 0;
        const alternativeUrls = getAlternativeTierUrls(tier, rank);

        if (currentRetryIndex < alternativeUrls.length - 1) {
            const nextIndex = currentRetryIndex + 1;
            const nextUrl = alternativeUrls[nextIndex];

            console.log(`대체 URL 시도 ${nextIndex}:`, nextUrl);

            setImageRetryIndex(prev => ({
                ...prev,
                [index]: nextIndex
            }));

            e.target.src = nextUrl;
        } else {
            console.error('모든 대체 URL 실패, 텍스트로 대체');
            e.target.style.display = 'none';

            // 텍스트 대체 요소 표시
            const textElement = e.target.nextElementSibling;
            if (textElement) {
                textElement.style.display = 'flex';
            }
        }
    };

    if (!leagueEntries || leagueEntries.length === 0) {
        return (
            <RankContainer>
                <RankTitle>랭크 정보</RankTitle>
                <UnrankedMessage>
                    <UnrankedImage
                        src="https://raw.communitydragon.org/latest/plugins/rcp-fe-lol-static-assets/global/default/images/ranked-mini-crests/unranked.png"
                        alt="언랭크"
                        onError={(e) => {
                            console.error('언랭크 이미지 로드 실패');
                            e.target.style.display = 'none';
                        }}
                    />
                    언랭크
                </UnrankedMessage>
            </RankContainer>
        );
    }

    const sortedEntries = [...leagueEntries].sort((a, b) => {
        const order = {
            'RANKED_SOLO_5x5': 1,
            'RANKED_FLEX_SR': 2,
        };
        return (order[a.queueType] || 99) - (order[b.queueType] || 99);
    });

    return (
        <RankContainer>
            <RankTitle>랭크 정보</RankTitle>
            {sortedEntries.map((entry, index) => {
                const winRate = calculateWinRate(entry.wins, entry.losses);
                const tierImageUrl = getTierImageUrl(entry.tier, entry.rank);
                const tierColor = getTierColor(entry.tier);
                const tierKorean = getTierKoreanName(entry.tier);
                const rankKorean = getRankKoreanName(entry.rank);

                // 실제 데이터 확인
                console.log(`=== 랭크 ${index + 1} 렌더링 ===`);
                console.log('Entry:', entry);
                console.log('Image URL:', tierImageUrl);

                return (
                    <RankItem key={index} queueType={entry.queueType}>
                        <LeftSection>
                            <QueueType>{getQueueTypeName(entry.queueType)}</QueueType>

                            <TierImageContainer>
                                <div style={{ position: 'relative' }}>
                                    <TierImage
                                        src={tierImageUrl}
                                        alt={`${entry.tier} ${entry.rank}`}
                                        onError={(e) => handleImageError(index, entry.tier, entry.rank, e)}
                                        onLoad={() => {
                                            console.log(`✅ 이미지 로드 성공: ${entry.tier} ${entry.rank}`);
                                        }}
                                    />

                                    {/* 텍스트 대체 요소 */}
                                    <TierTextFallback
                                        tierColor={tierColor}
                                        style={{ display: 'none' }}
                                    >
                                        {entry.tier.charAt(0)}
                                    </TierTextFallback>
                                </div>

                                <TierInfo>
                                    <TierDisplay tierColor={tierColor}>
                                        {tierKorean} {rankKorean}
                                    </TierDisplay>
                                    <LPDisplay>{entry.leaguePoints} LP</LPDisplay>
                                </TierInfo>
                            </TierImageContainer>
                        </LeftSection>

                        <WinRate>
                            <WinRatePercent rate={winRate}>{winRate}%</WinRatePercent>
                            <WinLoss>{entry.wins}승 {entry.losses}패</WinLoss>
                        </WinRate>
                    </RankItem>
                );
            })}
        </RankContainer>
    );
};

export default RankInfo;
