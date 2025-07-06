import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
    GlobalStyle,
    Container,
    SearchContainer,
    Title,
    SearchForm,
    SearchInput,
    TagInput,
    SearchButton,
    LoadingSpinner,
    ErrorMessage
} from '../styles/GlobalStyle';
import UserInfo from '../components/UserInfo';
import RankInfo from '../components/RankInfo';
import GameHistory from '../components/GameHistory';
import { getPlayerProfile, getGameHistory } from '../services/api';

function SearchResultPage() {
    const { gameName: urlGameName, tagLine: urlTagLine } = useParams();
    const navigate = useNavigate();

    const [gameName, setGameName] = useState(decodeURIComponent(urlGameName));
    const [tagLine, setTagLine] = useState(urlTagLine);
    const [playerProfile, setPlayerProfile] = useState(null);
    const [gameHistoryData, setGameHistoryData] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    // URL 파라미터가 변경될 때마다 검색 실행
    useEffect(() => {
        if (urlGameName && urlTagLine) {
            performSearch(decodeURIComponent(urlGameName), urlTagLine);
        }
    }, [urlGameName, urlTagLine]);

    // localStorage에서 상태 복원
    useEffect(() => {
        const savedState = localStorage.getItem('lolSearchState');
        if (savedState) {
            try {
                const parsedState = JSON.parse(savedState);
                if (parsedState.gameName === decodeURIComponent(urlGameName) &&
                    parsedState.tagLine === urlTagLine) {
                    setPlayerProfile(parsedState.playerProfile || null);
                    setGameHistoryData(parsedState.gameHistoryData || []);
                    console.log('=== 저장된 상태 복원 ===');
                }
            } catch (error) {
                console.error('저장된 상태 복원 실패:', error);
                localStorage.removeItem('lolSearchState');
            }
        }
    }, [urlGameName, urlTagLine]);

    // 상태 변경 시 localStorage에 저장
    useEffect(() => {
        if (playerProfile) {
            const stateToSave = {
                gameName: decodeURIComponent(urlGameName),
                tagLine: urlTagLine,
                playerProfile,
                gameHistoryData,
                timestamp: Date.now()
            };
            localStorage.setItem('lolSearchState', JSON.stringify(stateToSave));
        }
    }, [playerProfile, gameHistoryData, urlGameName, urlTagLine]);

    const performSearch = async (searchGameName, searchTagLine) => {
        setLoading(true);
        setError(null);

        try {
            console.log('=== 플레이어 프로필 조회 시작 ===');

            // 1. 플레이어 프로필 조회
            const profileData = await getPlayerProfile(searchGameName, searchTagLine);
            setPlayerProfile(profileData);

            // 2. 게임 기록 조회
            const historyData = await getGameHistory(searchGameName, searchTagLine, 20);
            setGameHistoryData(historyData);

            console.log('=== 검색 완료 ===');
        } catch (err) {
            console.error('검색 실패:', err);
            setError(err.response?.data?.message || '플레이어를 찾을 수 없습니다.');
            setPlayerProfile(null);
            setGameHistoryData([]);
            localStorage.removeItem('lolSearchState');
        } finally {
            setLoading(false);
        }
    };

    const handleNewSearch = async (e) => {
        e.preventDefault();
        if (!gameName.trim()) return;

        // URL 변경으로 새로운 검색 실행
        navigate(`/search/${encodeURIComponent(gameName.trim())}/${tagLine}`);
    };

    const handleGoHome = () => {
        localStorage.removeItem('lolSearchState');
        navigate('/');
    };

    return (
        <>
            <GlobalStyle />
            <Container>
                <SearchContainer isSearched={true}>
                    <Title isSearched={true}>롤 전적검색</Title>
                    <SearchForm onSubmit={handleNewSearch}>
                        <SearchInput
                            type="text"
                            placeholder="소환사명"
                            value={gameName}
                            onChange={(e) => setGameName(e.target.value)}
                            disabled={loading}
                        />
                        <TagInput
                            type="text"
                            placeholder="태그"
                            value={tagLine}
                            onChange={(e) => setTagLine(e.target.value)}
                            disabled={loading}
                        />
                        <SearchButton type="submit" disabled={loading || !gameName.trim()}>
                            {loading ? '검색중...' : '검색'}
                        </SearchButton>
                        <SearchButton
                            type="button"
                            onClick={handleGoHome}
                            style={{ marginLeft: '10px', background: '#6b7280' }}
                        >
                            새 검색
                        </SearchButton>
                    </SearchForm>
                </SearchContainer>

                {loading && <LoadingSpinner />}
                {error && <ErrorMessage>{error}</ErrorMessage>}

                {playerProfile && (
                    <>
                        <UserInfo playerProfile={playerProfile} />
                        <RankInfo leagueEntries={playerProfile.leagueEntries} />
                        {gameHistoryData.length > 0 && (
                            <GameHistory gameHistory={gameHistoryData} />
                        )}
                    </>
                )}
            </Container>
        </>
    );
}

export default SearchResultPage;
