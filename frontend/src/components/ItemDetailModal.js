/* src/components/ItemDetailModal.jsx */
import React, { useEffect, useState } from 'react';
import styled from 'styled-components';
import { getItemDetails, cleanDescription, formatItemStats } from '../utils/ItemUtils';

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
    max-width: 350px;
    width: 300px;
    max-height: 400px;
    overflow-y: auto;
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
    border: 1px solid #e0e0e0;
    pointer-events: auto;
    z-index: 10000;

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

const ItemImage = styled.img`
    width: 48px;
    height: 48px;
    border-radius: 8px;
    border: 2px solid #ddd;
`;

const ItemName = styled.h3`
    margin: 0;
    color: #333;
    font-size: 1.2rem;
`;

const ItemPrice = styled.div`
    color: #c89b3c;
    font-weight: bold;
    font-size: 0.9rem;
`;

const ItemStats = styled.div`
    margin: 16px 0;
`;

const StatItem = styled.div`
    display: flex;
    justify-content: space-between;
    padding: 4px 0;
    color: #0596aa;
    font-size: 0.9rem;
`;

const ItemDescription = styled.div`
    margin: 16px 0;
    line-height: 1.5;
    color: #666;
    font-size: 0.9rem;
`;

const ItemPlaintext = styled.div`
    margin: 12px 0;
    font-style: italic;
    color: #888;
    font-size: 0.85rem;
`;

const CloseButton = styled.button`
    position: absolute;
    top: 12px;
    right: 12px;
    background: none;
    border: none;
    font-size: 1.5rem;
    cursor: pointer;
    color: #999;
    
    &:hover {
        color: #333;
    }
`;

const LoadingSpinner = styled.div`
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 40px;
    color: #666;
`;
const ItemDetailModal = ({ itemId, onClose, isVisible = true, mousePosition }) => {
    const [itemData, setItemData] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const loadItemData = async () => {
            setLoading(true);
            const data = await getItemDetails(itemId);
            setItemData(data);
            setLoading(false);
        };

        if (itemId && itemId !== 0) {
            loadItemData();
        }
    }, [itemId]);

    if (!itemId || itemId === 0 || !isVisible) return null;

    // 마우스 위치 기준 모달 위치 계산
    const getModalStyle = () => {
        if (!mousePosition) return { top: '50%', left: '50%', transform: 'translate(-50%, -50%)' };

        const { x, y } = mousePosition;
        const offset = 15; // 마우스 커서로부터의 거리

        // 화면 경계 체크
        const modalWidth = 300;
        const modalHeight = 400;
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
                ) : itemData ? (
                    <>
                        <ModalHeader>
                            <ItemImage src={itemData.image} alt={itemData.name} />
                            <div>
                                <ItemName>{itemData.name}</ItemName>
                                {itemData.gold && (
                                    <ItemPrice>{itemData.gold.total}골드</ItemPrice>
                                )}
                            </div>
                        </ModalHeader>

                        {itemData.plaintext && (
                            <ItemPlaintext>{itemData.plaintext}</ItemPlaintext>
                        )}

                        {itemData.stats && (
                            <ItemStats>
                                <h4 style={{ margin: '0 0 8px 0', color: '#333' }}>스탯</h4>
                                {formatItemStats(itemData.stats).map((stat, index) => (
                                    <StatItem key={index}>
                                        <span>{stat.name}</span>
                                        <span>{stat.value}</span>
                                    </StatItem>
                                ))}
                            </ItemStats>
                        )}

                        {itemData.description && (
                            <ItemDescription>
                                <h4 style={{ margin: '0 0 8px 0', color: '#333' }}>효과</h4>
                                {cleanDescription(itemData.description)}
                            </ItemDescription>
                        )}
                    </>
                ) : (
                    <div>아이템 정보를 불러올 수 없습니다.</div>
                )}
            </ModalContent>
        </ModalOverlay>
    );
};

export default ItemDetailModal;