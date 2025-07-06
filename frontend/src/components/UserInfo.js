import React from 'react';
import styled from 'styled-components';

const UserCard = styled.div`
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 30px;
  box-shadow: 0 8px 24px rgba(0,0,0,0.1);
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

const UserInfo = ({ account }) => {
  return (
    <UserCard>
      <UserName>{account.gameName}</UserName>
      <UserTag>#{account.tagLine}</UserTag>
      <Puuid>PUUID: {account.puuid}</Puuid>
    </UserCard>
  );
};

export default UserInfo;
