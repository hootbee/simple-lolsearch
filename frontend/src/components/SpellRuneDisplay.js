/* src/components/SpellRuneDisplay.js */
import React from 'react';
import styled from 'styled-components';
import {
    getSummonerSpellImageUrl,
    getSummonerSpellInfo
} from '../utils/SpellUtils';
import {
    getKeystoneInfo,
    getRuneTreeInfo,
    getKeystoneImageUrl,
    getRuneTreeImageUrl
} from '../utils/RuneUtils';


/* ---------- Styled Components ---------- */
const SpellRuneContainer = styled.div`
    display: flex;
    flex-direction: column;
    gap: 4px;
    min-width: 70px;
    align-items: center;
`;

const SpellsContainer = styled.div`
    display: flex;
    gap: 2px;
`;

const SpellImage = styled.img`
    width: 35px;
    height: 35px;
    border-radius: 4px;
    border: 1px solid #ddd;
    cursor: help;

    &:hover {
        transform: scale(1.1);
        border-color: #007bff;
    }
`;

const RunesContainer = styled.div`
    display: flex;
    align-items: center;
    gap: 6px;  /* 간격 조정 */
`;

// 키스톤을 실제 이미지로 표시
const KeystoneImage = styled.img`
    width: 22px;
    height: 22px;
    border-radius: 50%;
    border: 2px solid ${({ borderColor }) => borderColor || '#c89b3c'};
    cursor: help;

    &:hover {
        transform: scale(1.2);
    }
`;

// 룬 트리를 실제 이미지로 표시
const RuneTreeImage = styled.img`
    width: 14px;
    height: 14px;
    border-radius: 50%;
    border: 1px solid #fff;
    cursor: help;

    &:hover {
        transform: scale(1.1);
    }
`;

/* ---------- Component ---------- */
const SpellRuneDisplay = ({
                              summonerSpell1Id,
                              summonerSpell2Id,
                              keystoneId,
                              primaryRuneTree,
                              secondaryRuneTree,
                              statRunes = [],
                              onSpellHover,  // 부모에서 받을 핸들러
                              onSpellHoverEnd // 부모에서 받을 핸들러
                          }) => {
    const spell1Info = getSummonerSpellInfo(summonerSpell1Id);
    const spell2Info = getSummonerSpellInfo(summonerSpell2Id);
    const keystoneInfo = getKeystoneInfo(keystoneId);
    const primaryTreeInfo = getRuneTreeInfo(primaryRuneTree);
    const secondaryTreeInfo = getRuneTreeInfo(secondaryRuneTree);

    return (
        <SpellRuneContainer>
            {/* 소환사 주문 - 부모 핸들러 사용 */}
            <SpellsContainer>
                <SpellImage
                    src={getSummonerSpellImageUrl(summonerSpell1Id)}
                    alt={spell1Info.name}
                    onMouseEnter={(e) => onSpellHover && onSpellHover(summonerSpell1Id, e)}
                    onMouseLeave={() => onSpellHoverEnd && onSpellHoverEnd()}
                    onError={(e) => {
                        e.target.src = getSummonerSpellImageUrl(4);
                    }}
                />
                <SpellImage
                    src={getSummonerSpellImageUrl(summonerSpell2Id)}
                    alt={spell2Info.name}
                    onMouseEnter={(e) => onSpellHover && onSpellHover(summonerSpell2Id, e)}
                    onMouseLeave={() => onSpellHoverEnd && onSpellHoverEnd()}
                    onError={(e) => {
                        e.target.src = getSummonerSpellImageUrl(4);
                    }}
                />
            </SpellsContainer>

            {/* 룬 정보 - 키스톤 + 보조 룬 트리만 */}
            <RunesContainer>
                <KeystoneImage
                    src={getKeystoneImageUrl(keystoneId)}
                    alt={keystoneInfo.name}
                    borderColor={primaryTreeInfo.color}
                    title={`${keystoneInfo.name} (${primaryTreeInfo.name})\n${keystoneInfo.description}`}
                    onError={(e) => {
                        e.target.src = getKeystoneImageUrl(8021);
                    }}
                />
                <RuneTreeImage
                    src={getRuneTreeImageUrl(secondaryRuneTree)}
                    alt={secondaryTreeInfo.name}
                    title={`보조 룬: ${secondaryTreeInfo.name} - ${secondaryTreeInfo.description}`}
                    onError={(e) => {
                        e.target.src = getRuneTreeImageUrl(8300);
                    }}
                />
            </RunesContainer>
        </SpellRuneContainer>
    );
};

export default SpellRuneDisplay;