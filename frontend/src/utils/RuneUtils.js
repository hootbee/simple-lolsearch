/* src/utils/RuneUtils.js */

// 룬 트리 정보
const TREES = {
    8000: { name: '정밀',   key: 'Precision',   color: '#c89b3c', description: '공격력·공속 중심' },
    8100: { name: '지배',   key: 'Domination',  color: '#dc143c', description: '폭딜·목표 접근' },
    8200: { name: '마법',   key: 'Sorcery',     color: '#6666ff', description: '주문력·마나 관리' },
    8300: { name: '결의',   key: 'Resolve',     color: '#00aa00', description: '방어력·생존력' },
    8400: { name: '영감',   key: 'Inspiration', color: '#49aab9', description: '창의적 도구·규칙 파괴' }
};

// 키스톤 룬 정보
const KEYSTONES = {
    // 정밀 (Precision)
    8005: { name: '집중 공격',      tree: 'Precision', description: '적 3회 연속 타격 시 추가 피해' },
    8008: { name: '치명적 속도',    tree: 'Precision', description: '공격 시 공격속도 증가, 한도 초과 가능' },
    8010: { name: '정복자',        tree: 'Precision', description: '전투 중 적응형 능력치 + 치유' },
    8021: { name: '기민한 발걸음',  tree: 'Precision', description: '적 공격·이동 시 스택, 충전 시 치유·이동속도' },

    // 지배 (Domination)
    8112: { name: '감전',          tree: 'Domination', description: '3회 개별 스킬/공격 적중 시 폭딜' },
    8124: { name: '포식자',        tree: 'Domination', description: '부여형 부츠 사용 후 돌진 + 추가 피해' },
    8128: { name: '어둠의 수확',    tree: 'Domination', description: '처치 관여 시 스택·추가 피해' },
    9923: { name: '칼날비',        tree: 'Domination', description: '단시간 공격속도 폭증' },

    // 마법 (Sorcery)
    8214: { name: '콩콩이 소환',    tree: 'Sorcery', description: '스킬 후 다음 공격·스킬에 추가 피해' },
    8229: { name: '신비로운 유성',  tree: 'Sorcery', description: '스킬 적중 시 유성 낙하' },
    8230: { name: '난입',          tree: 'Sorcery', description: '챔피언 접근/도주 시 이동속도 + 적응형 능력치' },

    // 결의 (Resolve)
    8437: { name: '착취의 손아귀',  tree: 'Resolve', description: '주기적 기본 공격에 체력 + 피해 + 영구 체력' },
    8439: { name: '여진',          tree: 'Resolve', description: '군중제어 후 방어·마저 상승 + 폭딜' },
    8465: { name: '수호자',        tree: 'Resolve', description: '아군 보호막 + 이동속도' },

    // 영감 (Inspiration)
    8351: { name: '빙결 강화',      tree: 'Inspiration', description: '이동 불가 효과 이후 적 둔화 + 고정 피해' },
    8360: { name: '봉인 풀린 주문서', tree: 'Inspiration', description: '소환사 주문 교체 가능' },
    8369: { name: '선제공격',      tree: 'Inspiration', description: '전투 개시 시 추가 골드 + 데미지' }
};

// 스탯 룬 정보
const STATS = {
    5001: { name: '체력',     icon: '❤️', value: '+15-140 체력',        description: '레벨당 체력 증가' },
    5002: { name: '방어력',   icon: '🛡️', value: '+6 방어력',          description: '물리 저항력 증가' },
    5003: { name: '마법 저항력', icon: '🔰', value: '+8 마법 저항력',    description: '마법 저항력 증가' },
    5005: { name: '공격속도', icon: '⚡', value: '+10% 공격속도',      description: '공격속도 증가' },
    5007: { name: '적응형 능력치', icon: '⚔️', value: '+8 주문·공격력', description: '높은 쪽 능력치 증가' },
    5008: { name: '적응형 능력치', icon: '🔮', value: '+9 적응형',      description: '높은 쪽 능력치 증가' },
    5013: { name: '적응형 능력치', icon: '⚔️', value: '+9 적응형',      description: '높은 쪽 능력치 증가' }
};

// 룬 조합 분석 데이터
const POPULAR_COMBOS = {
    'precision_domination': {
        roles: ['ADC', '정글', '탑'],
        style: '공격적',
        description: '딜링 + 기동성 극대화'
    },
    'sorcery_domination': {
        roles: ['미드', 'AP 캐리'],
        style: '폭발적',
        description: '순간 폭딜에 최적화'
    },
    'resolve_precision': {
        roles: ['탱커', '서포터'],
        style: '균형',
        description: '생존 + 지속딜'
    },
    'inspiration_resolve': {
        roles: ['서포터', '유틸'],
        style: '팀 기여',
        description: '효용성 극대화'
    }
};

// =================== EXPORT 함수들 ===================

/**
 * 룬 트리 정보 가져오기
 */
export const getRuneTreeInfo = (treeId) =>
    TREES[treeId] ?? {
        name: `알 수 없는 룬트리 ${treeId}`,
        key: 'Unknown',
        color: '#666',
        description: '룬 트리 정보를 찾을 수 없습니다.'
    };

/**
 * 키스톤 룬 정보 가져오기
 */
export const getKeystoneInfo = (runeId) =>
    KEYSTONES[runeId] ?? {
        name: `알 수 없는 룬 ${runeId}`,
        tree: 'Unknown',
        description: '룬 정보를 찾을 수 없습니다.'
    };

/**
 * 스탯 룬 정보 가져오기
 */
export const getStatRuneInfo = (statId) =>
    STATS[statId] ?? {
        name: `알 수 없는 스탯 ${statId}`,
        icon: '?',
        value: '알 수 없음',
        description: '스탯 룬 정보를 찾을 수 없습니다.'
    };

/**
 * 룬 조합 분석
 */
export const analyzeRuneCombination = (keystoneId, primaryTree, secondaryTree) => {
    const keystone  = getKeystoneInfo(keystoneId);
    const primary   = getRuneTreeInfo(primaryTree);
    const secondary = getRuneTreeInfo(secondaryTree);

    const comboKey  = `${primary.key.toLowerCase()}_${secondary.key.toLowerCase()}`;
    const combo     = POPULAR_COMBOS[comboKey] ?? {
        roles: ['특수'],
        style: '실험적',
        description: '드문 룬 조합'
    };

    return {
        keystone: keystone.name,
        primaryTree: primary.name,
        secondaryTree: secondary.name,
        combination: combo
    };
};

/**
 * 키스톤 룬 이미지 URL 생성
 */
export const getKeystoneImageUrl = (runeId) => {
    const baseUrl = 'https://ddragon.leagueoflegends.com/cdn/img/perk-images/Styles';
    const keystoneImages = {
        // 정밀
        8005: `${baseUrl}/Precision/PressTheAttack/PressTheAttack.png`,
        8008: `${baseUrl}/Precision/LethalTempo/LethalTempoTemp.png`,
        8021: `${baseUrl}/Precision/FleetFootwork/FleetFootwork.png`,
        8010: `${baseUrl}/Precision/Conqueror/Conqueror.png`,

        // 지배
        8112: `${baseUrl}/Domination/Electrocute/Electrocute.png`,
        8124: `${baseUrl}/Domination/Predator/Predator.png`,
        8128: `${baseUrl}/Domination/DarkHarvest/DarkHarvest.png`,
        9923: `${baseUrl}/Domination/HailOfBlades/HailOfBlades.png`,

        // 마법
        8214: `${baseUrl}/Sorcery/SummonAery/SummonAery.png`,
        8229: `${baseUrl}/Sorcery/ArcaneComet/ArcaneComet.png`,
        8230: `${baseUrl}/Sorcery/PhaseRush/PhaseRush.png`,

        // 결의
        8437: `${baseUrl}/Resolve/GraspOfTheUndying/GraspOfTheUndying.png`,
        8439: `${baseUrl}/Resolve/VeteranAftershock/VeteranAftershock.png`,
        8465: `${baseUrl}/Resolve/Guardian/Guardian.png`,

        // 영감
        8351: `${baseUrl}/Inspiration/GlacialAugment/GlacialAugment.png`,
        8360: `${baseUrl}/Inspiration/UnsealedSpellbook/UnsealedSpellbook.png`,
        8369: `${baseUrl}/Inspiration/FirstStrike/FirstStrike.png`
    };

    return keystoneImages[runeId] || `${baseUrl}/7201_Precision.png`;
};

/**
 * 룬 트리 이미지 URL 생성
 */
export const getRuneTreeImageUrl = (treeId) => {
    const baseUrl = 'https://ddragon.leagueoflegends.com/cdn/img/perk-images/Styles';
    const treeImages = {
        8000: `${baseUrl}/7201_Precision.png`,
        8100: `${baseUrl}/7200_Domination.png`,
        8200: `${baseUrl}/7202_Sorcery.png`,
        8300: `${baseUrl}/7204_Resolve.png`,
        8400: `${baseUrl}/7203_Whimsy.png`
    };

    return treeImages[treeId] || `${baseUrl}/7201_Precision.png`;
};

/**
 * 룬 트리별 색상 가져오기
 */
export const getRuneTreeColor = (treeId) => getRuneTreeInfo(treeId).color;

/**
 * 키스톤 룬 이름 가져오기
 */
export const getKeystoneName = (runeId) => getKeystoneInfo(runeId).name;
