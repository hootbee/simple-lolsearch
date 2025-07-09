/* src/utils/SpellUtils.js */
const DDRAGON_VERSION = '15.1.1';
const SPELL_IMAGE_BASE_URL =
    `https://ddragon.leagueoflegends.com/cdn/${DDRAGON_VERSION}/img/spell/`;

export const getSummonerSpellInfo = (id) => {
    const spells = {
        1:  {
            name: '정화',
            img: 'SummonerBoost.png',
            desc: '모든 디버프 제거 + 면역',
            description: '모든 디버프를 제거하고 잠시 동안 새로운 디버프에 면역이 됩니다.',
            cooldown: 210
        },
        3:  {
            name: '탈진',
            img: 'SummonerExhaust.png',
            desc: '이동속도·공격력 대폭 감소',
            description: '대상 적 챔피언의 이동속도를 30% 감소시키고, 공격력과 주문력을 40% 감소시킵니다.',
            cooldown: 210
        },
        4:  {
            name: '점멸',
            img: 'SummonerFlash.png',
            desc: '짧은 거리 순간이동',
            description: '마우스 커서 방향으로 짧은 거리를 순간이동합니다.',
            cooldown: 300
        },
        6:  {
            name: '유체화',
            img: 'SummonerHaste.png',
            desc: '이동속도 증가·유닛 통과',
            description: '이동속도가 증가하고 유닛을 통과할 수 있게 됩니다.',
            cooldown: 210
        },
        7:  {
            name: '회복',
            img: 'SummonerHeal.png',
            desc: '자신·아군 체력 회복',
            description: '자신과 가장 가까운 아군 챔피언의 체력을 회복하고 잠시 이동속도를 증가시킵니다.',
            cooldown: 240
        },
        11: {
            name: '강타',
            img: 'SummonerSmite.png',
            desc: '몬스터·미니언 고정 피해',
            description: '대상 몬스터나 미니언에게 고정 피해를 입힙니다. 정글 몬스터 처치 시 추가 효과가 있습니다.',
            cooldown: 90
        },
        12: {
            name: '순간이동',
            img: 'SummonerTeleport.png',
            desc: '아군 구조물/미니언 이동',
            description: '아군 구조물, 미니언, 와드로 순간이동합니다.',
            cooldown: 360
        },
        13: {
            name: '명료함',
            img: 'SummonerMana.png',
            desc: '마나 회복',
            description: '마나를 즉시 회복하고 모든 기본 능력의 재사용 대기시간을 감소시킵니다.',
            cooldown: 240
        },
        14: {
            name: '점화',
            img: 'SummonerDot.png',
            desc: '지속 피해 + 치유 감소',
            description: '대상 적 챔피언에게 지속 피해를 입히고 치유 효과를 감소시킵니다.',
            cooldown: 180
        },
        21: {
            name: '방어막',
            img: 'SummonerBarrier.png',
            desc: '짧은 보호막',
            description: '잠시 동안 피해를 흡수하는 보호막을 생성합니다.',
            cooldown: 210
        },
        32: {
            name: '눈덩이',
            img: 'SummonerSnowball.png',
            desc: '맞추면 돌진 (칼바람)',
            description: '눈덩이를 던져 맞춘 적에게 돌진할 수 있습니다. (칼바람 나락 전용)',
            cooldown: 80
        }
    };

    return spells[id] || {
        name: `스펠 ${id}`,
        img: 'SummonerFlash.png',
        desc: '정보 없음',
        description: '알 수 없는 소환사 주문입니다.',
        cooldown: 0
    };
};

export const getSummonerSpellImageUrl = (id) =>
    `${SPELL_IMAGE_BASE_URL}${getSummonerSpellInfo(id).img}`;
