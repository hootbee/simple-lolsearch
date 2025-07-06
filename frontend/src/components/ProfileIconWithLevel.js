import React from 'react';
import styled from 'styled-components';

const IconContainer = styled.div`
  position: relative;
  display: inline-block;
`;

const Icon = styled.img`
  width: ${props => props.size || '80px'};
  height: ${props => props.size || '80px'};
  border-radius: 12px;
  border: 3px solid #4f46e5;
  box-shadow: 0 4px 8px rgba(0,0,0,0.2);
`;

const Level = styled.div`
  position: absolute;
  bottom: -8px;
  left: 50%;
  transform: translateX(-50%);
  background: #4f46e5;
  color: white;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: ${props => props.size === '40px' ? '0.7rem' : '0.8rem'};
  font-weight: bold;
  min-width: 30px;
  text-align: center;
  border: 2px solid white;
  box-shadow: 0 2px 4px rgba(0,0,0,0.2);
`;

const ProfileIconWithLevel = ({ profileIconId, summonerLevel, size = '80px' }) => {
    const iconUrl = `http://ddragon.leagueoflegends.com/cdn/14.24.1/img/profileicon/${profileIconId}.png`;

    return (
        <IconContainer>
            <Icon
                src={iconUrl}
                size={size}
                alt="프로필 아이콘"
                onError={(e) => {
                    e.target.src = 'http://ddragon.leagueoflegends.com/cdn/14.24.1/img/profileicon/0.png';
                }}
            />
            <Level size={size}>{summonerLevel}</Level>
        </IconContainer>
    );
};

export default ProfileIconWithLevel;
