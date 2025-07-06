import React from 'react';
import styled from 'styled-components';
import { getTierImageUrl, getTierColor } from '../utils/tierUtils';

const TierContainer = styled.div`
  display: flex;
  align-items: center;
  gap: 8px;
`;

const TierImage = styled.img`
  width: ${props => props.size || '32px'};
  height: ${props => props.size || '32px'};
  object-fit: contain;
`;

const TierText = styled.span`
  font-weight: bold;
  color: ${props => props.tierColor};
  font-size: ${props => props.fontSize || '1rem'};
`;

const TierIcon = ({ tier, rank, showText = true, size = '32px', fontSize = '1rem' }) => {
    const tierImageUrl = getTierImageUrl(tier, rank);
    const tierColor = getTierColor(tier);

    return (
        <TierContainer>
            <TierImage
                src={tierImageUrl}
                size={size}
                alt={`${tier} ${rank}`}
                onError={(e) => {
                    e.target.src = getTierImageUrl('UNRANKED');
                }}
            />
            {showText && (
                <TierText tierColor={tierColor} fontSize={fontSize}>
                    {tier} {rank}
                </TierText>
            )}
        </TierContainer>
    );
};

export default TierIcon;
