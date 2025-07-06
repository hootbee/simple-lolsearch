import React from 'react';
import styled from 'styled-components';

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

const QueueType = styled.div`
  font-weight: bold;
  color: #333;
  font-size: 1.1rem;
`;

const TierInfo = styled.div`
  text-align: center;
`;

const TierDisplay = styled.div`
  font-size: 1.2rem;
  font-weight: bold;
  color: #333;
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
`;

const RankInfo = ({ leagueEntries }) => {
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

    if (!leagueEntries || leagueEntries.length === 0) {
        return (
            <RankContainer>
                <RankTitle>랭크 정보</RankTitle>
                <UnrankedMessage>언랭크</UnrankedMessage>
            </RankContainer>
        );
    }

    return (
        <RankContainer>
            <RankTitle>랭크 정보</RankTitle>
            {leagueEntries.map((entry, index) => {
                const winRate = calculateWinRate(entry.wins, entry.losses);

                return (
                    <RankItem key={index} queueType={entry.queueType}>
                        <QueueType>{getQueueTypeName(entry.queueType)}</QueueType>

                        <TierInfo>
                            <TierDisplay>{entry.tier} {entry.rank}</TierDisplay>
                            <LPDisplay>{entry.leaguePoints} LP</LPDisplay>
                        </TierInfo>

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
