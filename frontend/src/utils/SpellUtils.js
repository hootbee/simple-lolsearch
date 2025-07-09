/* src/utils/SpellUtils.js */
const DDRAGON_VERSION = '15.1.1';
const SPELL_IMAGE_BASE_URL =
    `https://ddragon.leagueoflegends.com/cdn/${DDRAGON_VERSION}/img/spell/`;

export const getSummonerSpellInfo = (id) => {
    const spells = {
        1:  { name: '정화',     img: 'SummonerBoost.png',   desc: '모든 디버프 제거 + 면역' },
        3:  { name: '탈진',     img: 'SummonerExhaust.png', desc: '이동속도·공격력 대폭 감소' },
        4:  { name: '점멸',     img: 'SummonerFlash.png',   desc: '짧은 거리 순간이동' },
        6:  { name: '유체화',   img: 'SummonerHaste.png',   desc: '이동속도 증가·유닛 통과' },
        7:  { name: '회복',     img: 'SummonerHeal.png',    desc: '자신·아군 체력 회복' },
        11: { name: '강타',     img: 'SummonerSmite.png',   desc: '몬스터·미니언 고정 피해' },
        12: { name: '순간이동', img: 'SummonerTeleport.png',desc: '아군 구조물/미니언 이동' },
        13: { name: '명료함',   img: 'SummonerMana.png',    desc: '마나 회복' },
        14: { name: '점화',     img: 'SummonerDot.png',     desc: '지속 피해 + 치유 감소' },
        21: { name: '방어막',   img: 'SummonerBarrier.png', desc: '짧은 보호막' },
        32: { name: '눈덩이',   img: 'SummonerSnowball.png',desc: '맞추면 돌진 (칼바람)' }
    };
    return spells[id] || { name: `스펠 ${id}`, img: 'SummonerFlash.png', desc: '정보 없음' };
};

export const getSummonerSpellImageUrl = (id) =>
    `${SPELL_IMAGE_BASE_URL}${getSummonerSpellInfo(id).img}`;

