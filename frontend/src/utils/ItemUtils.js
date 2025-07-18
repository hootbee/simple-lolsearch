// Data Dragon 버전 (최신 버전으로 업데이트 필요)
const DDRAGON_VERSION = '15.1.1';
const ITEM_IMAGE_BASE_URL = `https://ddragon.leagueoflegends.com/cdn/${DDRAGON_VERSION}/img/item/`;

/**
 * 아이템 ID를 이미지 URL로 변환
 */
export const getItemImageUrl = (itemId) => {
    if (!itemId || itemId === 0) {
        return null;
    }
    return `${ITEM_IMAGE_BASE_URL}${itemId}.png`;
};

/**
 * 빈 아이템 슬롯인지 확인
 */
export const isEmptyItem = (itemId) => {
    return !itemId || itemId === 0;
};

/**
 * 아이템 배열에서 빈 슬롯을 포함한 6칸 배열 생성
 */
export const normalizeItemArray = (items = []) => {
    const fullItems = [...items];
    while (fullItems.length < 6) {
        fullItems.push(0);
    }
    return fullItems.slice(0, 6);
};

/**
 * 아이템 총 개수 계산
 */
export const getTotalItems = (items = []) => {
    return items.filter(item => item && item !== 0).length;
};

/**
 * 모든 리그 오브 레전드 아이템 이름 매핑
 */
export const getItemName = (itemId) => {
    const itemNames = {
        // 기본 아이템 (1000번대)
        1001: '신발',
        1004: '요정의 부적',
        1006: '원기 회복의 구슬',
        1011: '거인의 허리띠',
        1018: '민첩성의 망토',
        1026: '블라스팅 완드',
        1027: '사파이어 수정',
        1028: '루비 수정',
        1029: '천 갑옷',
        1031: '쇠사슬 조끼',
        1033: '무효화 망토',
        1035: '롱소드',
        1036: '긴 칼',
        1037: '곡괭이',
        1038: 'B.F. 대검',
        1039: '사냥꾼의 칼날',
        1042: '단검',
        1043: '재귀의 활',
        1051: '폭풍갈퀴',
        1052: '증폭의 고서',
        1053: '흡혈의 낫',
        1054: '도란의 방패',
        1055: '도란의 검',
        1056: '도란의 반지',
        1057: '네가트론 망토',
        1058: '쓸데없이 큰 지팡이',
        1082: '어둠의 인장',
        1083: '칼날비',

        // 소모품 (2000번대)
        2003: '체력 물약',
        2031: '충전형 물약',
        2033: '타락한 물약',
        2055: '제어 와드',
        2138: '적응형 투구',
        2139: '적응형 조끼',
        2140: '적응형 망토',

        // 장화류 (3000번대 초반)
        3006: '광전사의 군화',
        3009: '신속의 장화',
        3020: '마법사의 신발',
        3047: '판금 장화',
        3111: '헤르메스의 발걸음',

        // Triumphant Boots 업그레이드 (추정 ID - 실제 확인 필요)
        224006: '무장진격', // Armored Advance (판금 장화 업그레이드)
        224009: '신속행진', // Swiftmarch (신속의 장화 업그레이드)
        224020: '주문술사의 신발', // Spellslinger's Shoes (마법사의 신발 업그레이드)
        224047: '강화 판금 장화', // Enhanced Plated Steelcaps
        224111: '강화 헤르메스의 발걸음', // Enhanced Mercury's Treads
        224158: '강화 이오니아의 장화', // Enhanced Ionian Boots

        // 전설 아이템 (3000번대)
        3001: '심연의 가면',
        3003: '대천사의 지팡이',
        3004: '마나무네',
        3005: '광휘의 검',
        3007: '천사의 눈물',
        3008: '마나무네',
        3010: '대천사의 포옹',
        3024: '빙하 갑옷',
        3025: '얼어붙은 심장',
        3026: '수호 천사',
        3031: '무한의 대검',
        3033: '필멸자의 운명',
        3035: '최후의 속삭임',
        3036: '도미닉 경의 인사',
        3040: '대천사의 지팡이',
        3041: '메자이의 영혼약탈자',
        3042: '무라마나',
        3043: '무라마나',
        3044: '파수꾼의 갑옷',
        3046: '유령 무희',
        3050: '지크의 전령',
        3053: '스테락의 도전',
        3057: '광휘의 검',
        3065: '정령의 형상',
        3067: '점화석',
        3068: '태양불꽃 방패',
        3071: '칠흑의 양날 도끼',
        3072: '피바라기',
        3074: '굶주린 히드라',
        3075: '가시 갑옷',
        3076: '브람블 조끼',
        3077: '티아맷',
        3078: '삼위일체',
        3082: '워모그의 갑옷',
        3083: '온기가 필요한 자',
        3084: '거대한 히드라',
        3085: '루난의 허리케인',
        3089: '라바돈의 죽음모자',
        3091: '마법사의 최후',
        3094: '고속 연사포',
        3095: '폭풍갈퀴',
        3100: '리치 베인',
        3102: '밴시의 장막',
        3105: '군단의 방패',
        3107: '구원',
        3108: '악마의 포옹',
        3109: '기사의 맹세',
        3110: '얼어붙은 심장',
        3115: '내셔의 이빨',
        3116: '라일라이의 수정홀',
        3117: '기동력의 장화',
        3118: '악의',
        3119: '겨울의 접근',
        3121: '칼날의 왕',
        3123: '처형인의 대검',
        3124: '구인수의 격노검',
        3128: '데마시아의 정의',
        3131: '요우무의 유령검',
        3133: '콜필드의 전쟁망치',
        3134: '톱날 단검',
        3135: '공허의 지팡이',
        3139: '헤르메스의 시미터',
        3140: '수은 장식띠',
        3142: '요우무의 유령검',
        3143: '란두인의 예언',
        3144: '빌지워터 해적검',
        3145: '마법공학 총검',
        3146: '마법공학 총검',
        3147: '시간 왜곡 물약',
        3152: '마법 공학 로켓 벨트',
        3153: '몰락한 왕의 검',
        3155: '마법공학 총검',
        3156: '헤르메스의 시미터',
        3157: '존야의 모래시계',
        3158: '이오니아의 장화',
        3161: '공허의 지팡이',
        3165: '모렐로노미콘',
        3172: '지크의 전령',
        3177: '수호자의 검',
        3179: '움브랄 글레이브',
        3181: '선체파괴자',
        3184: '수호자의 망치',
        3190: '강철의 솔라리 펜던트',
        3191: '추적자의 팔목 보호대',
        3193: '가고일 돌갑옷',
        3194: '적응형 투구',
        3196: '밤의 끝자락',

        // 신화급 아이템 (6000번대)
        6653: '리안드리의 고뇌',
        6655: '루덴의 동반자',
        6656: '만년서리',
        6657: '로드 도미닉의 인사',
        6662: '얼어붙은 건틀릿',
        6664: '터보 화공 탱크',
        6665: '해신 작쇼',
        6667: '광휘의 미덕',
        6671: '돌풍',
        6672: '크라켄 학살자',
        6673: '불멸의 철갑궁',
        6675: '나보리 신속검',
        6676: '징수의 총',
        6691: '드락사르의 황혼검',
        6692: '월식',
        6693: '독사의 송곳니',
        6694: '세릴다의 원한',
        6695: '독사의 송곳니',
        6696: '악마의 포옹',

        // 정글 아이템
        3715: '정글 아이템 - 적색 강타',
        3716: '정글 아이템 - 청색 강타',

        // 서포터 아이템
        3850: '강철 어깨 갑옷',
        3851: '룬강철 어깨갑옷',
        3853: '슈렐리아의 군가',
        3854: '강철의 솔라리 펜던트',
        3855: '월석 재생기',
        3857: '아테나의 성배',
        3858: '리바이어던',
        3859: '제국의 명령',
        3860: '슈렐리아의 진혼곡',
        3862: '기동력의 장화',
        3863: '얼어붙은 심장',
        3864: '수호자의 뿔피리',

        // 장신구 (와드)
        3340: '투명 와드',
        3341: '투명 와드',
        3363: '파수꾼의 와드',
        3364: '예언자의 렌즈',

        // 기타 특수 아이템
        4005: '제국의 명령',
        4628: '지평선의 초점',
        4629: '우주의 추진력',
        4630: '악마의 포옹',
        4632: '새벽심장',
        4633: '균열 생성기',
        4635: '밤의 수확자',
        4636: '밤의 수확자',
        4637: '악마의 포옹',
        4638: '감시하는 와드석',
        4641: '흐르는 물의 지팡이',
        4642: '흐르는 물의 지팡이',
        4643: '경계',
        4644: '왕관 분리자',
        4645: '그림자불꽃',
    };

    return itemNames[itemId] || `아이템 ${itemId}`;
};

/**
 * 아이템 카테고리 분류
 */
export const getItemCategory = (itemId) => {
    // 신발류
    if ([3006, 3009, 3020, 3047, 3111, 3117, 3158].includes(itemId)) {
        return 'boots';
    }

    // 새로운 Triumphant Boots (Tier 3 신발)
    if ([224006, 224009, 224020, 224047, 224111, 224158].includes(itemId)) {
        return 'triumphant_boots';
    }

    // 시작 아이템
    if ([1054, 1055, 1056].includes(itemId)) {
        return 'starter';
    }

    // 소모품
    if ([2003, 2031, 2033, 2055].includes(itemId)) {
        return 'consumable';
    }

    // 장신구
    if ([3340, 3341, 3363, 3364].includes(itemId)) {
        return 'trinket';
    }

    // 신화급 아이템
    if (itemId >= 6600 && itemId <= 6700) {
        return 'mythic';
    }

    // 전설급 아이템
    if (itemId >= 3000 && itemId < 4000) {
        return 'legendary';
    }

    // 기본 아이템
    if (itemId >= 1000 && itemId < 2000) {
        return 'basic';
    }

    return 'unknown';
};

/**
 * 아이템 등급별 테두리 색상
 */
export const getItemBorderColor = (itemId) => {
    const category = getItemCategory(itemId);

    switch (category) {
        case 'mythic':
            return '#c89b3c'; // 금색 (신화급)
        case 'legendary':
            return '#0596aa'; // 파란색 (전설급)
        case 'boots':
            return '#8b4513'; // 갈색 (신발)
        case 'consumable':
            return '#32cd32'; // 초록색 (소모품)
        case 'trinket':
            return '#ffa500'; // 주황색 (장신구)
        case 'starter':
            return '#808080'; // 회색 (시작 아이템)
        default:
            return '#ddd'; // 기본 회색
    }
};

/**
 * 아이템 가격 정보
 */
export const getItemPrice = (itemId) => {
    const itemPrices = {
        // 기본 아이템
        1001: 300,   // 신발
        1004: 350,   // 요정의 부적
        1006: 150,   // 원기 회복의 구슬
        1011: 380,   // 거인의 허리띠
        1018: 300,   // 민첩성의 망토
        1026: 850,   // 블라스팅 완드
        1027: 350,   // 사파이어 수정
        1028: 400,   // 루비 수정
        1029: 300,   // 천 갑옷
        1031: 800,   // 쇠사슬 조끼
        1033: 450,   // 무효화 망토
        1036: 350,   // 긴 칼
        1037: 875,   // 곡괭이
        1038: 1300,  // B.F. 대검
        1042: 300,   // 단검
        1043: 400,   // 재귀의 활
        1051: 500,   // 폭풍갈퀴
        1052: 435,   // 증폭의 고서
        1053: 900,   // 흡혈의 낫
        1054: 400,   // 도란의 방패
        1055: 450,   // 도란의 검
        1056: 400,   // 도란의 반지
        1057: 900,   // 네가트론 망토
        1058: 1250,  // 쓸데없이 큰 지팡이

        // 소모품
        2003: 50,    // 체력 물약
        2031: 500,   // 충전형 물약
        2033: 500,   // 타락한 물약
        2055: 75,    // 제어 와드

        // 신발류
        3006: 1100,  // 광전사의 군화
        3009: 1000,  // 신속의 장화
        3020: 1100,  // 마법사의 신발
        3047: 1100,  // 판금 장화
        3111: 1200,  // 헤르메스의 발걸음
        3117: 1000,  // 기동력의 장화
        3158: 1100,  // 이오니아의 장화

        // 전설급 아이템 (주요 아이템들)
        3031: 3400,  // 무한의 대검
        3078: 3333,  // 삼위일체
        3089: 3600,  // 라바돈의 죽음모자
        3153: 3200,  // 몰락한 왕의 검
        3157: 2600,  // 존야의 모래시계
        3165: 2500,  // 모렐로노미콘

        // 신화급 아이템
        6653: 3200,  // 리안드리의 고뇌
        6655: 3200,  // 루덴의 동반자
        6656: 2800,  // 만년서리
        6671: 3400,  // 돌풍
        6672: 3400,  // 크라켄 학살자
        6673: 3400,  // 불멸의 철갑궁
    };

    return itemPrices[itemId] || 0;
};

/**
 * 아이템 빌드 총 가격 계산
 */
export const calculateBuildCost = (items = []) => {
    return items.reduce((total, itemId) => {
        return total + getItemPrice(itemId);
    }, 0);
};

/**
 * 아이템이 활성 아이템인지 확인 (사용 가능한 아이템)
 */
export const isActiveItem = (itemId) => {
    const activeItems = [
        3153, // 몰락한 왕의 검
        3157, // 존야의 모래시계
        3142, // 요우무의 유령검
        3146, // 헥스테크 총검
        3152, // 헥스테크 로켓 벨트
        3190, // 강철의 솔라리 펜던트
        3193, // 가고일 돌갑옷
        6653, // 리안드리의 고뇌
        6655, // 루덴의 동반자
        6671, // 돌풍
        6672, // 크라켄 학살자
        // 더 많은 활성 아이템들...
    ];

    return activeItems.includes(itemId);
};

export const getItemDetails = async (itemId) => {
    if (!itemId || itemId === 0) return null;

    try {
        const response = await fetch(
            `https://ddragon.leagueoflegends.com/cdn/${DDRAGON_VERSION}/data/ko_KR/item.json`
        );
        const data = await response.json();
        const item = data.data[itemId];

        if (!item) return null;

        return {
            id: itemId,
            name: item.name,
            description: item.description,
            plaintext: item.plaintext,
            stats: item.stats,
            gold: item.gold,
            tags: item.tags,
            image: `${ITEM_IMAGE_BASE_URL}${itemId}.png`
        };
    } catch (error) {
        console.error('아이템 정보 로드 실패:', error);
        return null;
    }
};

export const cleanDescription = (description) => {
    if (!description) return '';
    return description
        .replace(/<[^>]*>/g, '')
        .replace(/&nbsp;/g, ' ')
        .replace(/&lt;/g, '<')
        .replace(/&gt;/g, '>')
        .trim();
};

export const formatItemStats = (stats) => {
    if (!stats) return [];

    const statNames = {
        FlatHPPoolMod: '체력',
        FlatMPPoolMod: '마나',
        FlatPhysicalDamageMod: '공격력',
        FlatMagicDamageMod: '주문력',
        FlatArmorMod: '방어력',
        FlatSpellBlockMod: '마법 저항력',
        FlatCritChanceMod: '치명타 확률',
        FlatAttackSpeedMod: '공격속도',
        FlatMovementSpeedMod: '이동속도',
        PercentLifeStealMod: '생명력 흡수'
    };

    return Object.entries(stats)
        .map(([key, value]) => {
            const name = statNames[key] || key;
            const formattedValue = key.includes('Percent') ? `${(value * 100).toFixed(1)}%` : `+${value}`;
            return { name, value: formattedValue };
        });
};