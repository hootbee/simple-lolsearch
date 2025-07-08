import React from 'react';
import styled from 'styled-components';
import ItemSlot from './ItemSlot';
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

const ItemBuild = ({ items = [], trinket = 0, size = 28, showCount = false }) => {
    const normalizedItems = normalizeItemArray(items);
    const itemCount = getTotalItems(items);

    return (
        <ItemBuildContainer>
            <ItemGrid>
                {normalizedItems.map((itemId, index) => (
                    <ItemSlot
                        key={index}
                        itemId={itemId}
                        size={size}
                    />
                ))}
            </ItemGrid>

            {trinket !== 0 && (
                <TrinketSlot>
                    <ItemSlot
                        itemId={trinket}
                        size={size}
                    />
                </TrinketSlot>
            )}

            {showCount && (
                <ItemCount>
                    {itemCount}/6
                </ItemCount>
            )}
        </ItemBuildContainer>
    );
};

export default ItemBuild;
