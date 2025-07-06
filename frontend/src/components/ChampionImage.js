import React, { useState } from 'react';
import styled from 'styled-components';
import { getChampionImageUrl, getAlternativeChampionUrls, normalizeChampionName } from '../utils/championUtils';

const ChampionImageContainer = styled.div`
  position: relative;
  display: inline-block;
`;

const ChampionImg = styled.img`
  width: ${props => props.size || '32px'};
  height: ${props => props.size || '32px'};
  border-radius: 50%;
  border: 2px solid #4f46e5;
  object-fit: cover;
  box-shadow: 0 2px 4px rgba(0,0,0,0.2);
`;

const ChampionNameFallback = styled.div`
  width: ${props => props.size || '32px'};
  height: ${props => props.size || '32px'};
  border-radius: 50%;
  border: 2px solid #4f46e5;
  background: #f3f4f6;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: ${props => props.size === '64px' ? '0.8rem' : '0.6rem'};
  font-weight: bold;
  color: #374151;
`;

const ChampionImage = ({ championName, size = '32px', showName = false }) => {
    const [retryIndex, setRetryIndex] = useState(0);
    const [imageError, setImageError] = useState(false);

    const normalizedName = normalizeChampionName(championName);
    const imageUrl = getChampionImageUrl(normalizedName);

    const handleImageError = (e) => {
        console.error(`챔피언 이미지 로드 실패: ${championName} (${normalizedName})`);

        const alternatives = getAlternativeChampionUrls(normalizedName);

        if (retryIndex < alternatives.length - 1) {
            const nextIndex = retryIndex + 1;
            const nextUrl = alternatives[nextIndex];

            console.log(`대체 URL 시도 ${nextIndex}:`, nextUrl);
            setRetryIndex(nextIndex);
            e.target.src = nextUrl;
        } else {
            console.error('모든 대체 URL 실패, 텍스트로 대체');
            setImageError(true);
        }
    };

    if (imageError) {
        return (
            <ChampionImageContainer>
                <ChampionNameFallback size={size}>
                    {championName.substring(0, 2)}
                </ChampionNameFallback>
                {showName && <span style={{ marginLeft: '8px' }}>{championName}</span>}
            </ChampionImageContainer>
        );
    }

    return (
        <ChampionImageContainer>
            <ChampionImg
                src={imageUrl}
                alt={championName}
                size={size}
                onError={handleImageError}
                onLoad={() => {
                    console.log(`✅ 챔피언 이미지 로드 성공: ${championName}`);
                }}
            />
            {showName && <span style={{ marginLeft: '8px' }}>{championName}</span>}
        </ChampionImageContainer>
    );
};

export default ChampionImage;
