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
import {
    getPlayerProfile,
    getGameHistory,
    refreshPlayerProfile,
    loadMoreGameHistoryByPuuid
} from '../services/api';

function SearchResultPage() {
    const { gameName: urlGameName, tagLine: urlTagLine } = useParams();
    const navigate = useNavigate();

    // 기본 상태
    const [gameName, setGameName] = useState(urlGameName ? decodeURIComponent(urlGameName) : '');
    const [tagLine, setTagLine] = useState(urlTagLine || 'KR1');
    const [playerProfile, setPlayerProfile] = useState(null);
    const [gameHistoryData, setGameHistoryData] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [refreshing, setRefreshing] = useState(false);

    // 🔥 PUUID 상태 추가
    const [userPuuid, setUserPuuid] = useState(null);

    // 무한 스크롤 관련 상태
    const [loadingMore, setLoadingMore] = useState(false);
    const [hasMoreGames, setHasMoreGames] = useState(true);
    const [loadMoreError, setLoadMoreError] = useState(null);

    // URL 파라미터 변경 시 검색 실행
    useEffect(() => {
        if (urlGameName && urlTagLine) {
            try {
                const decodedGameName = decodeURIComponent(urlGameName);
                performSearch(decodedGameName, urlTagLine);
            } catch (decodeError) {
                console.error('URL 디코딩 실패:', decodeError);
                setError('잘못된 URL 형식입니다.');
            }
        }
    }, [urlGameName, urlTagLine]);

    // 로컬 스토리지에서 상태 복원
    useEffect(() => {
        if (!urlGameName || !urlTagLine) return;

        const savedState = localStorage.getItem('lolSearchState');
        if (savedState) {
            try {
                const parsedState = JSON.parse(savedState);
                const decodedGameName = decodeURIComponent(urlGameName);

                if (parsedState.gameName === decodedGameName &&
                    parsedState.tagLine === urlTagLine) {
                    setPlayerProfile(parsedState.playerProfile || null);
                    setGameHistoryData(parsedState.gameHistoryData || []);
                    setUserPuuid(parsedState.userPuuid || null); // 🔥 PUUID 복원
                    setHasMoreGames(parsedState.hasMoreGames !== false);

                    console.log('=== 저장된 상태 복원 ===');
                    console.log('복원된 프로필:', parsedState.playerProfile);
                    console.log('복원된 게임 히스토리:', parsedState.gameHistoryData);
                    console.log('복원된 PUUID:', parsedState.userPuuid);
                }
            } catch (error) {
                console.error('저장된 상태 복원 실패:', error);
                localStorage.removeItem('lolSearchState');
            }
        }
    }, [urlGameName, urlTagLine]);

    // 상태 변경 시 로컬 스토리지에 저장
    useEffect(() => {
        if (playerProfile && urlGameName && urlTagLine && userPuuid) {
            try {
                const stateToSave = {
                    gameName: decodeURIComponent(urlGameName),
                    tagLine: urlTagLine,
                    playerProfile,
                    gameHistoryData,
                    userPuuid, // 🔥 PUUID 저장
                    hasMoreGames,
                    timestamp: Date.now()
                };
                localStorage.setItem('lolSearchState', JSON.stringify(stateToSave));
                console.log('=== 상태 저장 완료 ===');
                console.log('저장된 데이터:', stateToSave);
            } catch (error) {
                console.error('상태 저장 실패:', error);
            }
        }
    }, [playerProfile, gameHistoryData, userPuuid, hasMoreGames, urlGameName, urlTagLine]);

    // 검색 실행 함수
    const performSearch = async (searchGameName, searchTagLine) => {
        if (!searchGameName || !searchTagLine) {
            setError('소환사명과 태그라인을 모두 입력해주세요.');
            return;
        }

        setLoading(true);
        setError(null);
        setLoadMoreError(null);
        setHasMoreGames(true);

        try {
            console.log('=== 플레이어 프로필 조회 시작 ===');
            console.log('검색 대상:', { searchGameName, searchTagLine });

            // 1. 프로필 조회
            const profileData = await getPlayerProfile(searchGameName, searchTagLine);
            setPlayerProfile(profileData);

            // 🔥 PUUID 추출 및 저장
            const puuid = profileData.account?.puuid;
            if (!puuid) {
                throw new Error('PUUID를 찾을 수 없습니다.');
            }
            setUserPuuid(puuid);
            console.log('✅ 프로필 조회 완료:', profileData);
            console.log('🔑 PUUID 추출:', puuid);

            // 2. 게임 히스토리 조회
            console.log('=== 게임 히스토리 조회 시작 ===');
            const historyData = await getGameHistory(searchGameName, searchTagLine, 10);
            setGameHistoryData(historyData);
            setHasMoreGames(historyData.length >= 10);

            console.log('✅ 게임 히스토리 조회 완료:', historyData);
            console.log('게임 히스토리 개수:', historyData.length);

            console.log('=== 전체 조회 완료 ===');
        } catch (err) {
            console.error('❌ 검색 실패:', err);
            setError(err.message || '플레이어를 찾을 수 없습니다.');
            setPlayerProfile(null);
            setGameHistoryData([]);
            setUserPuuid(null);
            setHasMoreGames(false);
            localStorage.removeItem('lolSearchState');
        } finally {
            setLoading(false);
        }
    };

    // 🔥 PUUID 기반 더보기 핸들러
    const handleLoadMore = async ({ puuid, lastGameTime, count = 5 }) => {
        if (loadingMore || !hasMoreGames || !puuid) return;

        setLoadingMore(true);
        setLoadMoreError(null);

        try {
            console.log('=== PUUID 기반 추가 게임 히스토리 로드 시작 ===');
            console.log('요청 파라미터:', { puuid, lastGameTime, count });

            const moreGames = await loadMoreGameHistoryByPuuid(puuid, lastGameTime, count);

            if (moreGames.length > 0) {
                setGameHistoryData(prev => [...prev, ...moreGames]);

                // 요청한 수보다 적게 받았으면 더 이상 없다고 판단
                if (moreGames.length < count) {
                    setHasMoreGames(false);
                }

                console.log('✅ 추가 게임 로드 완료:', moreGames.length, '게임');
            } else {
                setHasMoreGames(false);
                console.log('더 이상 로드할 게임이 없습니다');
            }
        } catch (err) {
            console.error('❌ 추가 게임 로드 실패:', err);
            setLoadMoreError(err.message || '추가 게임을 불러오는 중 오류가 발생했습니다.');
        } finally {
            setLoadingMore(false);
        }
    };

    // 프로필 갱신 함수
    const handleRefresh = async () => {
        if (!playerProfile || !urlGameName || !urlTagLine) return;

        setRefreshing(true);
        setError(null);
        setLoadMoreError(null);
        setHasMoreGames(true);

        try {
            console.log('=== 프로필 강제 갱신 시작 ===');

            // 1. 프로필 갱신
            const refreshedProfile = await refreshPlayerProfile(
                decodeURIComponent(urlGameName),
                urlTagLine
            );
            setPlayerProfile(refreshedProfile);

            // 🔥 PUUID 재추출
            const puuid = refreshedProfile.account?.puuid;
            setUserPuuid(puuid);
            console.log('✅ 프로필 갱신 완료:', refreshedProfile);

            // 2. 게임 히스토리 갱신
            console.log('=== 게임 히스토리 갱신 시작 ===');
            const historyData = await getGameHistory(
                decodeURIComponent(urlGameName),
                urlTagLine,
                20
            );
            setGameHistoryData(historyData);
            setHasMoreGames(historyData.length >= 20);

            console.log('✅ 게임 히스토리 갱신 완료:', historyData);
            console.log('갱신된 게임 히스토리 개수:', historyData.length);

            console.log('=== 전체 갱신 완료 ===');
        } catch (err) {
            console.error('❌ 갱신 실패:', err);
            setError(err.message || '프로필 갱신 중 오류가 발생했습니다.');
        } finally {
            setRefreshing(false);
        }
    };

    // 새로운 검색 실행
    const handleNewSearch = async (e) => {
        e.preventDefault();
        const trimmedGameName = gameName.trim();
        const trimmedTagLine = tagLine.trim();

        if (!trimmedGameName) {
            setError('소환사명을 입력해주세요.');
            return;
        }

        if (!trimmedTagLine) {
            setError('태그라인을 입력해주세요.');
            return;
        }

        console.log('=== 새로운 검색 시작 ===');
        console.log('검색어:', { trimmedGameName, trimmedTagLine });

        navigate(`/search/${encodeURIComponent(trimmedGameName)}/${trimmedTagLine}`);
    };

    // 홈으로 이동
    const handleGoHome = () => {
        console.log('=== 홈으로 이동 ===');
        localStorage.removeItem('lolSearchState');
        navigate('/');
    };

    // 재시도 함수
    const handleRetry = () => {
        if (urlGameName && urlTagLine) {
            try {
                const decodedGameName = decodeURIComponent(urlGameName);
                performSearch(decodedGameName, urlTagLine);
            } catch (error) {
                setError('URL 디코딩 중 오류가 발생했습니다.');
            }
        }
    };

    return (
        <>
            <GlobalStyle />
            <Container>
                {/* 검색 영역 */}
                <SearchContainer isSearched={true}>
                    <Title isSearched={true}>롤 전적검색</Title>
                    <SearchForm onSubmit={handleNewSearch}>
                        <SearchInput
                            type="text"
                            placeholder="소환사명"
                            value={gameName}
                            onChange={(e) => setGameName(e.target.value)}
                            disabled={loading || refreshing}
                        />
                        <TagInput
                            type="text"
                            placeholder="태그"
                            value={tagLine}
                            onChange={(e) => setTagLine(e.target.value)}
                            disabled={loading || refreshing}
                        />
                        <SearchButton type="submit" disabled={loading || refreshing || !gameName.trim()}>
                            {loading ? '검색중...' : '검색'}
                        </SearchButton>
                        <SearchButton
                            type="button"
                            onClick={handleGoHome}
                            style={{ marginLeft: '10px', background: '#6b7280' }}
                            disabled={loading || refreshing}
                        >
                            홈으로
                        </SearchButton>
                    </SearchForm>
                </SearchContainer>

                {/* 로딩 스피너 */}
                {(loading || refreshing) && (
                    <div style={{ textAlign: 'center', padding: '20px' }}>
                        <LoadingSpinner />
                        <p style={{ marginTop: '10px', color: '#6b7280' }}>
                            {loading ? '플레이어 정보와 게임 히스토리를 검색하고 있습니다...' : '데이터를 갱신하고 있습니다...'}
                        </p>
                    </div>
                )}

                {/* 에러 메시지 */}
                {error && <ErrorMessage>{error}</ErrorMessage>}

                {/* 플레이어 프로필 결과 */}
                {playerProfile && (
                    <>
                        {/* 갱신 영역 */}
                        <div style={{
                            display: 'flex',
                            justifyContent: 'space-between',
                            alignItems: 'center',
                            padding: '15px 20px',
                            background: '#f8fafc',
                            border: '1px solid #e2e8f0',
                            borderRadius: '8px',
                            margin: '20px 0'
                        }}>
                            <div>
                                <h3 style={{ margin: '0 0 5px 0', fontSize: '16px', color: '#1e293b' }}>
                                    {playerProfile.account?.gameName || '알 수 없음'}#
                                    {playerProfile.account?.tagLine || '알 수 없음'}
                                </h3>
                                <p style={{ margin: 0, fontSize: '14px', color: '#64748b' }}>
                                    마지막 업데이트: {new Date().toLocaleString()}
                                </p>
                            </div>
                            <div>
                                <SearchButton
                                    type="button"
                                    onClick={handleRefresh}
                                    style={{
                                        background: '#10b981',
                                        fontSize: '14px',
                                        padding: '8px 16px'
                                    }}
                                    disabled={loading || refreshing}
                                >
                                    {refreshing ? '갱신중...' : '🔄 데이터 갱신'}
                                </SearchButton>
                            </div>
                        </div>

                        {/* 프로필 정보 */}
                        <UserInfo playerProfile={playerProfile} />
                        <RankInfo leagueEntries={playerProfile.leagueEntries || []} />

                        {/* 🔥 게임 히스토리 표시 - PUUID 기반 무한 스크롤 */}
                        {gameHistoryData.length > 0 && (
                            <div style={{ margin: '20px 0' }}>
                                <h3 style={{
                                    color: '#1e293b',
                                    marginBottom: '15px',
                                    fontSize: '18px',
                                    fontWeight: '600'
                                }}>
                                    🎮 최근 게임 기록 ({gameHistoryData.length}게임)
                                </h3>
                                <GameHistory
                                    gameHistory={gameHistoryData}
                                    onLoadMore={handleLoadMore}
                                    loading={loadingMore}
                                    hasMore={hasMoreGames}
                                    error={loadMoreError}
                                    puuid={userPuuid} // 🔥 PUUID 전달
                                />
                            </div>
                        )}

                        {/* DB 테스트용 정보 표시 */}
                        <div style={{
                            padding: '20px',
                            background: '#f0f9ff',
                            border: '1px solid #0ea5e9',
                            borderRadius: '8px',
                            margin: '20px 0'
                        }}>
                            <h4 style={{ color: '#0369a1', marginBottom: '15px' }}>
                                🔍 DB 캐시 테스트 정보
                            </h4>
                            <div style={{ display: 'grid', gap: '8px' }}>
                                <p style={{ margin: 0, fontSize: '14px' }}>
                                    <strong>PUUID:</strong>
                                    <span style={{
                                        fontFamily: 'monospace',
                                        background: '#e0f2fe',
                                        padding: '2px 6px',
                                        borderRadius: '4px',
                                        marginLeft: '8px'
                                    }}>
                                        {userPuuid || '정보 없음'}
                                    </span>
                                </p>
                                <p style={{ margin: 0, fontSize: '14px' }}>
                                    <strong>소환사 레벨:</strong> {playerProfile.summonerLevel || '정보 없음'}
                                </p>
                                <p style={{ margin: 0, fontSize: '14px' }}>
                                    <strong>랭크 정보 개수:</strong> {playerProfile.leagueEntries?.length || 0}개
                                </p>
                                <p style={{ margin: 0, fontSize: '14px' }}>
                                    <strong>게임 히스토리 개수:</strong> {gameHistoryData.length}게임
                                </p>
                                <p style={{ margin: 0, fontSize: '14px' }}>
                                    <strong>프로필 아이콘 ID:</strong> {playerProfile.profileIconId || '정보 없음'}
                                </p>
                                <p style={{ margin: 0, fontSize: '14px' }}>
                                    <strong>더보기 가능:</strong> {hasMoreGames ? '예' : '아니오'}
                                </p>
                                <p style={{ margin: 0, fontSize: '14px' }}>
                                    <strong>더보기 로딩 중:</strong> {loadingMore ? '예' : '아니오'}
                                </p>
                                <div style={{ marginTop: '10px', padding: '10px', background: '#e0f2fe', borderRadius: '4px' }}>
                                    <p style={{ margin: 0, fontSize: '14px', color: '#059669', fontWeight: '500' }}>
                                        ✅ 프로필 데이터가 DB에서 로드되었습니다.
                                    </p>
                                    <p style={{ margin: '5px 0 0 0', fontSize: '14px', color: '#059669', fontWeight: '500' }}>
                                        ✅ 게임 히스토리 데이터가 PUUID 기반으로 로드되었습니다.
                                    </p>
                                    <p style={{ margin: '5px 0 0 0', fontSize: '14px', color: '#059669', fontWeight: '500' }}>
                                        ✅ PUUID 기반 무한 스크롤 기능이 활성화되었습니다.
                                    </p>
                                </div>
                            </div>
                        </div>
                    </>
                )}

                {/* 에러 상황에서의 재시도 영역 */}
                {error && !playerProfile && (
                    <div style={{
                        textAlign: 'center',
                        padding: '40px 20px',
                        background: '#fef2f2',
                        border: '1px solid #fecaca',
                        borderRadius: '8px',
                        margin: '20px 0'
                    }}>
                        <h3 style={{ color: '#dc2626', marginBottom: '10px' }}>
                            데이터를 불러올 수 없습니다
                        </h3>
                        <p style={{ color: '#6b7280', marginBottom: '20px' }}>
                            {error}
                        </p>
                        <SearchButton
                            type="button"
                            onClick={handleRetry}
                            style={{ background: '#ef4444' }}
                            disabled={loading || refreshing}
                        >
                            {loading ? '재시도중...' : '🔄 다시 시도'}
                        </SearchButton>
                    </div>
                )}

                {/* 게임 히스토리가 없을 때 안내 */}
                {playerProfile && gameHistoryData.length === 0 && !loading && !error && (
                    <div style={{
                        textAlign: 'center',
                        padding: '30px 20px',
                        background: '#f9fafb',
                        border: '1px solid #e5e7eb',
                        borderRadius: '8px',
                        margin: '20px 0'
                    }}>
                        <h3 style={{ color: '#374151', marginBottom: '10px' }}>
                            게임 기록이 없습니다
                        </h3>
                        <p style={{ color: '#6b7280', fontSize: '14px' }}>
                            최근 게임 기록을 찾을 수 없습니다. 게임을 플레이한 후 다시 확인해보세요.
                        </p>
                    </div>
                )}
            </Container>
        </>
    );
}

export default SearchResultPage;
