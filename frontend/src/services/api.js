import axios from 'axios';

// 환경변수 우선, 없으면 NODE_ENV 기반 자동 설정
const getApiBaseUrl = () => {
    // 환경변수가 있으면 우선 사용
    if (process.env.REACT_APP_API_BASE_URL) {
        return process.env.REACT_APP_API_BASE_URL;
    }

    // 환경변수가 없으면 NODE_ENV 기반 자동 설정
    if (process.env.NODE_ENV === 'production') {
        return 'http://3.27.207.48:8080/api';
    } else {
        return 'http://localhost:8080/api';
    }
};

const API_BASE_URL = getApiBaseUrl();

const api = axios.create({
    baseURL: API_BASE_URL,
    timeout: 10000,
});

// 개발 시 현재 설정 확인용 로그
if (process.env.NODE_ENV === 'development') {
    console.log('🔗 API Base URL:', API_BASE_URL);
    console.log('🌍 Environment:', process.env.NODE_ENV);
}

export const searchSummoner = async (gameName, tagLine) => {
    const response = await api.get('/summoner/account', {
        params: { gameName, tagLine }
    });
    return response.data;
};

export const getPlayerProfile = async (gameName, tagLine) => {
    const response = await api.get('/summoner/profile', {
        params: { gameName, tagLine }
    });
    return response.data;
};

export const getLeagueEntries = async (puuid) => {
    const response = await api.get('/summoner/league', {
        params: { puuid }
    });
    return response.data;
};

export const getGameHistory = async (gameName, tagLine, count = 20) => {
    const response = await api.get('/summoner/game-history', {
        params: { gameName, tagLine, count }
    });
    return response.data;
};
export const getGameDetail = async (matchId) => {
    const response = await api.get(`/summoner/game-detail/${matchId}`);
    return response.data;
};
