/* src/components/SpellDetailModal.jsx */
import React, { useState} from 'react';
import styled from 'styled-components';
import { getSummonerSpellInfo, getSummonerSpellImageUrl } from '../utils/SpellUtils';
import { useEffect } from 'react';

const ModalOverlay = styled.div`
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: transparent;
    z-index: 9999;
    pointer-events: none;
`;

const ModalContent = styled.div`
    position: absolute;
    background: white;
    border-radius: 12px;
    padding: 20px;
    max-width: 300px;
    width: 250px;
    max-height: 300px;
    overflow-y: auto;
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
    border: 1px solid #e0e0e0;
    pointer-events: auto;
    z-index: 10000;
    transition: opacity 0.1s ease-in-out; /* 부드러운 등장을 위한 트랜지션 */

    /* 화살표 스타일 */
    &::before {
        content: '';
        position: absolute;
        top: -8px;
        left: 20px;
        width: 0;
        height: 0;
        border-left: 8px solid transparent;
        border-right: 8px solid transparent;
        border-bottom: 8px solid white;
        filter: drop-shadow(0 -2px 2px rgba(0, 0, 0, 0.1));
    }
`;

const ModalHeader = styled.div`
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 16px;
    padding-bottom: 12px;
    border-bottom: 1px solid #eee;
`;

const SpellImage = styled.img`
    width: 48px;
    height: 48px;
    border-radius: 8px;
    border: 2px solid #ddd;
`;

const SpellName = styled.h3`
    margin: 0;
    color: #333;
    font-size: 1.1rem;
`;

const SpellCooldown = styled.div`
    color: #666;
    font-size: 0.9rem;
    margin-top: 4px;
`;

const SpellDescription = styled.div`
    line-height: 1.5;
    color: #666;
    font-size: 0.9rem;
`;

const LoadingSpinner = styled.div`
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 40px;
    color: #666;
`;

const SpellDetailModal = ({ spellId, onClose, isVisible = true, mousePosition }) => {
    const [spellData, setSpellData] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (spellId) {
            setSpellData(getSummonerSpellInfo(spellId));
            setLoading(false);
        }
    }, [spellId]);

    if (!spellId || !isVisible) return null;

    // ItemDetailModal과 동일한 위치 계산 로직
    const getModalStyle = () => {
        if (!mousePosition) return { top: '50%', left: '50%', transform: 'translate(-50%, -50%)' };

        const { x, y } = mousePosition;
        const offset = 15; // 마우스 커서로부터의 거리

        // 화면 경계 체크
        const modalWidth = 250;
        const modalHeight = 300;
        const windowWidth = window.innerWidth;
        const windowHeight = window.innerHeight;

        let modalLeft = x + offset;
        let modalTop = y + offset;

        // 오른쪽 경계 체크
        if (modalLeft + modalWidth > windowWidth) {
            modalLeft = x - modalWidth - offset;
        }

        // 아래쪽 경계 체크
        if (modalTop + modalHeight > windowHeight) {
            modalTop = y - modalHeight - offset;
        }

        // 최소 여백 보장
        modalLeft = Math.max(10, modalLeft);
        modalTop = Math.max(10, modalTop);

        return {
            top: `${modalTop}px`,
            left: `${modalLeft}px`,
            transform: 'none'
        };
    };

    return (
        <ModalOverlay>
            <ModalContent
                style={getModalStyle()}
                onMouseEnter={() => {}} // 모달 위에서는 닫히지 않도록
                onMouseLeave={onClose}   // 모달에서 마우스가 벗어나면 닫기
            >
                {loading ? (
                    <LoadingSpinner>로딩 중...</LoadingSpinner>
                ) : spellData ? (
                    <>
                        <ModalHeader>
                            <SpellImage
                                src={getSummonerSpellImageUrl(spellId)}
                                alt={spellData.name}
                                onError={(e) => {
                                    e.target.src = getSummonerSpellImageUrl(4);
                                }}
                            />
                            <div>
                                <SpellName>{spellData.name}</SpellName>
                                <SpellCooldown>쿨다운: {spellData.cooldown}초</SpellCooldown>
                            </div>
                        </ModalHeader>
                        <SpellDescription>
                            {spellData.description}
                        </SpellDescription>
                    </>
                ) : (
                    <div>스펠 정보를 불러올 수 없습니다.</div>
                )}
            </ModalContent>
        </ModalOverlay>
    );
};

export default SpellDetailModal;
