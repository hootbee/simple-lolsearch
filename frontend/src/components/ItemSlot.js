/* src/components/ItemSlot.jsx */
import React from 'react';
import styled from 'styled-components';
import { getItemImageUrl, getItemName } from '../utils/ItemUtils';

const ItemSlotContainer = styled.div`
    width: ${props => props.size}px;
    height: ${props => props.size}px;
    border: 1px solid ${props => props.borderColor};
    border-radius: 4px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: ${props => props.isEmpty ? '#f8f9fa' : '#fff'};
    position: relative;
    cursor: ${props => props.isEmpty ? 'default' : 'pointer'};
    transition: all 0.2s ease;

    &:hover {
        transform: ${props => props.isEmpty ? 'none' : 'scale(1.05)'};
        border-color: ${props => props.isEmpty ? props.borderColor : '#007bff'};
        box-shadow: ${props => props.isEmpty ? 'none' : '0 2px 8px rgba(0,123,255,0.3)'};
    }
`;

const ItemImage = styled.img`
    width: 100%;
    height: 100%;
    object-fit: cover;
    border-radius: 3px;
`;

const EmptySlot = styled.div`
    width: 60%;
    height: 60%;
    background: #e9ecef;
    border-radius: 2px;
`;

const ActiveIndicator = styled.div`
    position: absolute;
    top: -2px;
    right: -2px;
    width: 8px;
    height: 8px;
    background: #28a745;
    border-radius: 50%;
    border: 1px solid white;
`;
const ItemSlot = ({ itemId, size = 32, isActive = false, onHover, onHoverEnd, onClick }) => {
    const isEmpty = !itemId || itemId === 0;
    const itemName = getItemName(itemId);
    const imageUrl = getItemImageUrl(itemId);

    const getBorderColor = () => {
        if (isEmpty) return '#dee2e6';
        if (isActive) return '#28a745';
        return '#ced4da';
    };

    const handleMouseEnter = (event) => {
        if (!isEmpty && onHover) {
            onHover(itemId, event);
        }
    };

    const handleMouseLeave = () => {
        if (!isEmpty && onHoverEnd) {
            onHoverEnd();
        }
    };

    const handleClick = () => {
        if (!isEmpty && onClick) {
            onClick(itemId);
        }
    };

    return (
        <ItemSlotContainer
            isEmpty={isEmpty}
            size={size}
            borderColor={getBorderColor()}
            onMouseEnter={handleMouseEnter}
            onMouseLeave={handleMouseLeave}
            onClick={handleClick}
        >
            {isEmpty ? (
                <EmptySlot />
            ) : (
                <>
                    <ItemImage
                        src={imageUrl}
                        alt={itemName}
                        onError={(e) => {
                            e.target.src = 'https://ddragon.leagueoflegends.com/cdn/15.1.1/img/ui/empty.png';
                        }}
                    />
                    {isActive && <ActiveIndicator />}
                </>
            )}
        </ItemSlotContainer>
    );
};

export default ItemSlot;