import React from 'react';
import styled from 'styled-components';
import { getItemImageUrl, getItemName, isEmptyItem } from '../utils/ItemUtils';

const ItemSlotContainer = styled.div`
    width: ${props => props.size}px;
    height: ${props => props.size}px;
    border: 1px solid #ddd;
    border-radius: 4px;
    background: ${props => props.isEmpty ? '#f5f5f5' : 'transparent'};
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
    overflow: hidden;
    cursor: ${props => props.isEmpty ? 'default' : 'help'};
`;

const ItemImage = styled.img`
    width: 100%;
    height: 100%;
    object-fit: cover;
    border-radius: 3px;
`;

const EmptySlot = styled.div`
    width: 100%;
    height: 100%;
    background: linear-gradient(45deg, #f0f0f0 25%, transparent 25%),
                linear-gradient(-45deg, #f0f0f0 25%, transparent 25%),
                linear-gradient(45deg, transparent 75%, #f0f0f0 75%),
                linear-gradient(-45deg, transparent 75%, #f0f0f0 75%);
    background-size: 6px 6px;
    background-position: 0 0, 0 3px, 3px -3px, -3px 0px;
`;

const ItemSlot = ({ itemId, size = 32 }) => {
    const isEmpty = isEmptyItem(itemId);
    const imageUrl = getItemImageUrl(itemId);
    const itemName = getItemName(itemId);

    return (
        <ItemSlotContainer
            isEmpty={isEmpty}
            size={size}
            title={isEmpty ? '빈 슬롯' : itemName}
        >
            {isEmpty ? (
                <EmptySlot />
            ) : (
                <ItemImage
                    src={imageUrl}
                    alt={itemName}
                    onError={(e) => {
                        e.target.style.display = 'none';
                        e.target.parentElement.innerHTML =
                            `<div style="font-size: ${size * 0.3}px; color: #999; text-align: center;">?</div>`;
                    }}
                />
            )}
        </ItemSlotContainer>
    );
};

export default ItemSlot;
