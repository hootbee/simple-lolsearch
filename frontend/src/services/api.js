import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
    baseURL: API_BASE_URL,
    timeout: 10000,
});

export const searchSummoner = async (gameName, tagLine) => {
    const response = await api.get('/summoner/account', {
        params: { gameName, tagLine }
    });
    return response.data;
};

// 새로 추가: 플레이어 프로필 조회
export const getPlayerProfile = async (gameName, tagLine) => {
    const response = await api.get('/summoner/profile', {
        params: { gameName, tagLine }
    });
    return response.data;
};

// 새로 추가: 리그 정보만 조회
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
