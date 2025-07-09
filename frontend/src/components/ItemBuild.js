/* src/components/ItemBuild.jsx */
import React, { useState } from 'react';
import styled from 'styled-components';
import ItemSlot from './ItemSlot';
import ItemDetailModal from './ItemDetailModal';
import { normalizeItemArray, getTotalItems } from '../utils/ItemUtils';

const ItemBuildContainer = styled.div`
    display: flex;
    gap: 2px;
    align-items: center;
`;

const ItemGrid = styled.div`
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    grid-template-rows: repeat(2, 1fr);
    gap: 2px;
`;

const TrinketSlot = styled.div`
    margin-left: 4px;
    border-left: 1px solid #eee;
    padding-left: 4px;
`;

const ItemCount = styled.span`
    font-size: 0.7rem;
    color: #666;
    margin-left: 4px;
`;

const ItemBuild = ({
                       items = [],
                       trinket = 0,
                       size = 28,
                       showCount = false,
                       onItemHover,
                       onItemHoverEnd
                   }) => {
    const [selectedItemId, setSelectedItemId] = useState(null);

    const normalizedItems = normalizeItemArray(items);
    const itemCount = getTotalItems(items);

    // 클릭 핸들러 (기존 기능 유지)
    const handleItemClick = (itemId) => {
        setSelectedItemId(itemId);
    };

    const handleCloseModal = () => {
        setSelectedItemId(null);
    };

    // 호버 핸들러 - 마우스 이벤트 전달
    const handleItemHover = (itemId, event) => {
        if (onItemHover) {
            onItemHover(itemId, event);
        }
    };

    const handleItemHoverEnd = () => {
        if (onItemHoverEnd) {
            onItemHoverEnd();
        }
    };

    return (
        <>
            <ItemBuildContainer>
                <ItemGrid>
                    {normalizedItems.map((itemId, index) => (
                        <ItemSlot
                            key={index}
                            itemId={itemId}
                            size={size}
                            onClick={handleItemClick}
                            onHover={handleItemHover}
                            onHoverEnd={handleItemHoverEnd}
                        />
                    ))}
                </ItemGrid>

                {trinket !== 0 && (
                    <TrinketSlot>
                        <ItemSlot
                            itemId={trinket}
                            size={size}
                            onClick={handleItemClick}
                            onHover={handleItemHover}
                            onHoverEnd={handleItemHoverEnd}
                        />
                    </TrinketSlot>
                )}

                {showCount && (
                    <ItemCount>
                        {itemCount}/6
                    </ItemCount>
                )}
            </ItemBuildContainer>

            {/* 클릭 시 표시되는 모달만 유지 (중앙에 표시) */}
            {selectedItemId && (
                <ItemDetailModal
                    itemId={selectedItemId}
                    onClose={handleCloseModal}
                />
            )}
        </>
    );
};

export default ItemBuild;