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

export const getGameHistory = async (gameName, tagLine, count = 20) => {
    const response = await api.get('/summoner/game-history', {
        params: { gameName, tagLine, count }
    });
    return response.data;
};
