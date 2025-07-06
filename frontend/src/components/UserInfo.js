import React from 'react';
import styled from 'styled-components';

const UserCard = styled.div`
    background: white;
    border-radius: 12px;
    padding: 24px;
    margin-bottom: 30px;
    box-shadow: 0 8px 24px rgba(0,0,0,0.1);
    display: flex;
    align-items: center;
    gap: 20px;
`;

const ProfileIconContainer = styled.div`
    position: relative;
`;

const ProfileIcon = styled.img`
    width: 80px;
    height: 80px;
    border-radius: 12px;
    border: 3px solid #4f46e5;
    box-shadow: 0 4px 8px rgba(0,0,0,0.2);
`;

const LevelBadge = styled.div`
    position: absolute;
    bottom: -8px;
    left: 50%;
    transform: translateX(-50%);
    background: #4f46e5;
    color: white;
    padding: 4px 8px;
    border-radius: 12px;
    font-size: 0.8rem;
    font-weight: bold;
    min-width: 30px;
    text-align: center;
    border: 2px solid white;
    box-shadow: 0 2px 4px rgba(0,0,0,0.2);
`;

const UserDetails = styled.div`
  flex: 1;
`;

const UserName = styled.h2`
  font-size: 1.8rem;
  color: #333;
  margin-bottom: 8px;
`;

const UserTag = styled.span`
  color: #666;
  font-size: 1.1rem;
`;

const Puuid = styled.p`
  color: #999;
  font-size: 0.9rem;
  margin-top: 8px;
  word-break: break-all;
`;

const LastUpdated = styled.p`
  color: #999;
  font-size: 0.8rem;
  margin-top: 4px;
`;

const UserInfo = ({ playerProfile }) => {
    const { account, profileIconId, summonerLevel, revisionDate } = playerProfile;

    // 프로필 아이콘 URL 생성
    const profileIconUrl = `http://ddragon.leagueoflegends.com/cdn/14.24.1/img/profileicon/${profileIconId}.png`;

    // 마지막 업데이트 시간 포맷팅
    const formatLastUpdated = (timestamp) => {
        const date = new Date(timestamp);
        return date.toLocaleDateString('ko-KR') + ' ' + date.toLocaleTimeString('ko-KR');
    };

    return (
        <UserCard>
            <ProfileIconContainer>
                <ProfileIcon
                    src={profileIconUrl}
                    alt="프로필 아이콘"
                    onError={(e) => {
                        e.target.src = 'http://ddragon.leagueoflegends.com/cdn/14.24.1/img/profileicon/0.png';
                    }}
                />
                <LevelBadge>{summonerLevel}</LevelBadge>
            </ProfileIconContainer>

            <UserDetails>
                <UserName>{account.gameName}</UserName>
                <UserTag>#{account.tagLine}</UserTag>
                <Puuid>PUUID: {account.puuid}</Puuid>
                <LastUpdated>
                    마지막 업데이트: {formatLastUpdated(revisionDate)}
                </LastUpdated>
            </UserDetails>
        </UserCard>
    );
};

export default UserInfo;
