import axios from 'axios';

// 환경변수 우선, 없으면 NODE_ENV 기반 자동 설정
const getApiBaseUrl = () => {
    if (process.env.REACT_APP_API_BASE_URL) {
        return process.env.REACT_APP_API_BASE_URL;
    }

    if (process.env.NODE_ENV === 'production') {
        return 'http://3.27.207.48:8080/api';
    } else {
        return 'http://localhost:8080/api';
    }
};

const API_BASE_URL = getApiBaseUrl();

const api = axios.create({
    baseURL: API_BASE_URL,
    timeout: 30000,
});

// 개발 시 현재 설정 확인용 로그
if (process.env.NODE_ENV === 'development') {
    console.log('🔗 API Base URL:', API_BASE_URL);
    console.log('🌍 Environment:', process.env.NODE_ENV);
}

// API 응답 처리 헬퍼 함수
const handleApiResponse = (response) => {
    if (response.data.success) {
        return response.data.data;
    } else {
        throw new Error(response.data.message || '요청 처리 중 오류가 발생했습니다.');
    }
};

// 에러 처리 헬퍼 함수
const handleApiError = (error) => {
    if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
    }
    throw error;
};

// 1. 계정 정보 조회
export const searchSummoner = async (gameName, tagLine) => {
    try {
        const response = await api.get('/summoner/account', {
            params: { gameName, tagLine }
        });
        return response.data;
    } catch (error) {
        handleApiError(error);
    }
};

// 2. 최근 매치 ID 조회 (누락된 엔드포인트 추가)
export const getRecentMatches = async (puuid, count = 10) => {
    try {
        const response = await api.get('/summoner/matches', {
            params: { puuid, count }
        });
        return response.data;
    } catch (error) {
        handleApiError(error);
    }
};

// 3. 플레이어 프로필 조회
export const getPlayerProfile = async (gameName, tagLine) => {
    try {
        const response = await api.get('/summoner/profile', {
            params: { gameName, tagLine }
        });
        return handleApiResponse(response);
    } catch (error) {
        handleApiError(error);
    }
};

// 4. 리그 정보 조회
export const getLeagueEntries = async (puuid) => {
    try {
        const response = await api.get('/summoner/league', {
            params: { puuid }
        });
        return response.data;
    } catch (error) {
        handleApiError(error);
    }
};

// 5. 게임 히스토리 조회
export const getGameHistory = async (gameName, tagLine, count = 20) => {
    try {
        console.log('🎮 게임 히스토리 API 호출:', { gameName, tagLine, count });
        const response = await api.get('/summoner/game-history', {
            params: { gameName, tagLine, count }
        });
        console.log('✅ 게임 히스토리 응답:', response.data);
        return response.data;
    } catch (error) {
        console.error('❌ 게임 히스토리 조회 실패:', error);
        handleApiError(error);
    }
};

// 6. 게임 상세 조회
export const getGameDetail = async (matchId) => {
    try {
        console.log('🔍 게임 상세 API 호출:', matchId);
        const response = await api.get(`/summoner/game-detail/${matchId}`);
        console.log('✅ 게임 상세 응답:', response.data);
        return response.data; // 컨트롤러에서 ApiResponse 사용하지 않음
    } catch (error) {
        console.error('❌ 게임 상세 조회 실패:', error);
        handleApiError(error);
    }
};

// 7. 프로필 갱신
export const refreshPlayerProfile = async (gameName, tagLine) => {
    try {
        console.log('🔄 프로필 갱신 API 호출:', { gameName, tagLine });
        const response = await api.post('/summoner/profile/refresh', null, {
            params: { gameName, tagLine }
        });
        console.log('✅ 프로필 갱신 응답:', response.data);
        return handleApiResponse(response);
    } catch (error) {
        console.error('❌ 프로필 갱신 실패:', error);
        handleApiError(error);
    }
};


// 9. 이전 경기 더보기 (PUUID 직접 방식 - 성능 최적화)
export const loadMoreGameHistoryByPuuid = async (puuid, lastGameTime, count = 5) => {
    try {
        console.log('🚀 PUUID 기반 이전 경기 더보기 API 호출:', { puuid, lastGameTime, count });
        const response = await api.get('/summoner/game-history/load-more', {
            params: { puuid, lastGameTime, count }
        });
        console.log('✅ PUUID 기반 이전 경기 더보기 응답:', response.data);
        return response.data;
    } catch (error) {
        console.error('❌ PUUID 기반 이전 경기 더보기 실패:', error);
        handleApiError(error);
    }
};

// services/api.js에서 추가
export const getAccountByPuuid = async (puuid) => {
    try {
        console.log('🔍 PUUID로 계정 정보 조회:', puuid);
        const response = await api.get('/summoner/account/by-puuid', {
            params: { puuid }
        });
        console.log('✅ PUUID 계정 조회 응답:', response.data);
        return response.data;
    } catch (error) {
        console.error('❌ PUUID 계정 조회 실패:', error);
        handleApiError(error);
    }
};

// 🔥 새로 추가: PUUID 기반 게임 히스토리 조회
export const getGameHistoryByPuuid = async (puuid, count = 20) => {
    try {
        console.log('🚀 PUUID 기반 게임 히스토리 API 호출:', { puuid, count });
        const response = await api.get('/summoner/game-history/by-puuid', {
            params: { puuid, count }
        });
        console.log('✅ PUUID 게임 히스토리 응답:', response.data);
        return response.data;
    } catch (error) {
        console.error('❌ PUUID 게임 히스토리 조회 실패:', error);
        handleApiError(error);
    }
};

// 🔥 새로 추가: Queue ID 기반 게임 히스토리 조회
export const getGameHistoryByQueueId = async (puuid, queueId, page = 0, size = 10) => {
    try {
        console.log('🚀 Queue ID 기반 게임 히스토리 API 호출:', { puuid, queueId, page, size });
        const response = await api.get('/summoner/game-history/by-queue', {
            params: { puuid, queueId, page, size }
        });
        console.log('✅ Queue ID 기반 게임 히스토리 응답:', response.data);
        return response.data;
    } catch (error) {
        console.error('❌ Queue ID 기반 게임 히스토리 조회 실패:', error);
        handleApiError(error);
    }
};
