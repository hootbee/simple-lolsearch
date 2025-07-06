// 올바른 Community Dragon URL 사용
export const getTierImageUrl = (tier, rank) => {
    console.log('=== getTierImageUrl 호출 ===');
    console.log('Input tier:', tier);
    console.log('Input rank:', rank);

    if (!tier || tier === 'UNRANKED') {
        const url = 'https://raw.communitydragon.org/latest/plugins/rcp-fe-lol-static-assets/global/default/images/ranked-mini-crests/unranked.png';
        console.log('Unranked URL:', url);
        return url;
    }

    const tierLower = tier.toLowerCase();

    // 검색 결과에서 확인한 올바른 경로
    const url = `https://raw.communitydragon.org/latest/plugins/rcp-fe-lol-static-assets/global/default/images/ranked-mini-crests/${tierLower}.png`;
    console.log('Generated URL:', url);
    return url;
};

// 대체 URL들
export const getAlternativeTierUrls = (tier, rank) => {
    const tierLower = tier.toLowerCase();
    const tierCapitalized = tier.charAt(0).toUpperCase() + tier.slice(1).toLowerCase();

    return [
        // Community Dragon (메인)
        `https://raw.communitydragon.org/latest/plugins/rcp-fe-lol-static-assets/global/default/images/ranked-mini-crests/${tierLower}.png`,

        // 검색 결과에서 확인한 emblem 경로
        `https://raw.communitydragon.org/14.6/plugins/rcp-fe-lol-static-assets/global/default/ranked-emblem/emblem-${tierLower}.png`,

        // Data Dragon 공식
        `https://ddragon.leagueoflegends.com/cdn/14.24.1/img/ranked-emblems/Emblem_${tierCapitalized}.png`,

        // GitHub CDN
        `https://cdn.jsdelivr.net/gh/magisteriis/lol-icons-and-emblems/ranked-emblems/Emblem_${tierCapitalized}.png`,

        // 기본 언랭크 이미지
        'https://raw.communitydragon.org/latest/plugins/rcp-fe-lol-static-assets/global/default/images/ranked-mini-crests/unranked.png'
    ];
};

// 티어 색상 반환 함수
export const getTierColor = (tier) => {
    const tierColors = {
        'IRON': '#8B4513',
        'BRONZE': '#CD7F32',
        'SILVER': '#C0C0C0',
        'GOLD': '#FFD700',
        'PLATINUM': '#00CED1',
        'EMERALD': '#00FF7F',
        'DIAMOND': '#B9F2FF',
        'MASTER': '#9932CC',
        'GRANDMASTER': '#FF4500',
        'CHALLENGER': '#F0E68C'
    };

    return tierColors[tier] || '#666';
};

// 티어 한글명 반환 함수
export const getTierKoreanName = (tier) => {
    const tierNames = {
        'IRON': '아이언',
        'BRONZE': '브론즈',
        'SILVER': '실버',
        'GOLD': '골드',
        'PLATINUM': '플래티넘',
        'EMERALD': '에메랄드',
        'DIAMOND': '다이아몬드',
        'MASTER': '마스터',
        'GRANDMASTER': '그랜드마스터',
        'CHALLENGER': '챌린저'
    };

    return tierNames[tier] || tier;
};

// 랭크 한글명 반환 함수
export const getRankKoreanName = (rank) => {
    const rankNames = {
        'I': '1',
        'II': '2',
        'III': '3',
        'IV': '4',
        'V': '5'
    };

    return rankNames[rank] || rank;
};

// 프로필 아이콘 URL
export const getProfileIconUrl = (profileIconId) => {
    return `https://raw.communitydragon.org/latest/game/assets/ux/summonericons/profileicon${profileIconId}.png`;
};

// 디버깅용 함수
export const debugTierImage = (tier, rank) => {
    console.log('=== 티어 이미지 디버깅 ===');
    console.log('Tier:', tier);
    console.log('Rank:', rank);
    console.log('Generated URL:', getTierImageUrl(tier, rank));
    console.log('Korean Name:', getTierKoreanName(tier), getRankKoreanName(rank));
    console.log('Color:', getTierColor(tier));
    console.log('Alternative URLs:', getAlternativeTierUrls(tier, rank));
};

// 이미지 URL 유효성 검사 함수
export const validateImageUrl = async (url) => {
    try {
        const response = await fetch(url, { method: 'HEAD' });
        return response.ok;
    } catch (error) {
        console.error('URL 검증 실패:', url, error);
        return false;
    }
};

// 최적의 이미지 URL 찾기 함수
export const findBestTierImageUrl = async (tier, rank) => {
    const urls = [getTierImageUrl(tier, rank), ...getAlternativeTierUrls(tier, rank)];

    for (const url of urls) {
        const isValid = await validateImageUrl(url);
        if (isValid) {
            console.log('✅ 유효한 이미지 URL 발견:', url);
            return url;
        }
    }

    console.warn('⚠️ 모든 이미지 URL 실패, 기본 이미지 사용');
    return 'https://raw.communitydragon.org/latest/plugins/rcp-fe-lol-static-assets/global/default/images/ranked-mini-crests/unranked.png';
};
