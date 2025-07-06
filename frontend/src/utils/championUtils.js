// Data Dragon을 사용한 챔피언 이미지 URL 생성
export const getChampionImageUrl = (championName) => {
    if (!championName) {
        return 'https://ddragon.leagueoflegends.com/cdn/14.24.1/img/champion/Aatrox.png'; // 기본 이미지
    }

    // 검색 결과에서 확인한 URL 패턴 사용
    return `http://ddragon.leagueoflegends.com/cdn/14.24.1/img/champion/${championName}.png`;
};

// 대체 챔피언 이미지 URL들
export const getAlternativeChampionUrls = (championName) => {
    return [
        // Data Dragon 공식
        `http://ddragon.leagueoflegends.com/cdn/14.24.1/img/champion/${championName}.png`,

        // Community Dragon
        `https://raw.communitydragon.org/latest/plugins/rcp-be-lol-game-data/global/default/v1/champion-icons/${championName.toLowerCase()}.png`,

        // 다른 버전 시도
        `http://ddragon.leagueoflegends.com/cdn/13.24.1/img/champion/${championName}.png`,

        // 기본 이미지
        'https://ddragon.leagueoflegends.com/cdn/14.24.1/img/champion/Aatrox.png'
    ];
};

// 챔피언 이름 정규화 (특수문자 처리)
export const normalizeChampionName = (championName) => {
    // 일부 챔피언은 특별한 처리가 필요
    const nameMap = {
        'Wukong': 'MonkeyKing',
        'Nunu & Willump': 'Nunu',
        'Cho\'Gath': 'Chogath',
        'Kai\'Sa': 'Kaisa',
        'Kha\'Zix': 'Khazix',
        'Kog\'Maw': 'KogMaw',
        'LeBlanc': 'Leblanc',
        'Vel\'Koz': 'Velkoz',
        'Rek\'Sai': 'RekSai'
    };

    return nameMap[championName] || championName;
};
