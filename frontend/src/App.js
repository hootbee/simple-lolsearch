import React, { useState, useEffect } from 'react';
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
} from './styles/GlobalStyle';
import UserInfo from './components/UserInfo';
import RankInfo from './components/RankInfo';
import GameHistory from './components/GameHistory';
import { getPlayerProfile, getGameHistory } from './services/api';

function App() {
  const [gameName, setGameName] = useState('');
  const [tagLine, setTagLine] = useState('KR1');
  const [playerProfile, setPlayerProfile] = useState(null);
  const [gameHistoryData, setGameHistoryData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [isSearched, setIsSearched] = useState(false);

  // localStorage에서 상태 복원
  useEffect(() => {
    const savedState = localStorage.getItem('lolSearchState');
    if (savedState) {
      try {
        const parsedState = JSON.parse(savedState);
        setGameName(parsedState.gameName || '');
        setTagLine(parsedState.tagLine || 'KR1');
        setPlayerProfile(parsedState.playerProfile || null);
        setGameHistoryData(parsedState.gameHistoryData || []);
        setIsSearched(parsedState.isSearched || false);

        console.log('=== 저장된 상태 복원 ===');
        console.log('복원된 데이터:', parsedState);
      } catch (error) {
        console.error('저장된 상태 복원 실패:', error);
        localStorage.removeItem('lolSearchState');
      }
    }
  }, []);

  // 상태 변경 시 localStorage에 저장
  useEffect(() => {
    if (isSearched && playerProfile) {
      const stateToSave = {
        gameName,
        tagLine,
        playerProfile,
        gameHistoryData,
        isSearched,
        timestamp: Date.now()
      };

      localStorage.setItem('lolSearchState', JSON.stringify(stateToSave));
      console.log('=== 상태 저장 완료 ===');
    }
  }, [gameName, tagLine, playerProfile, gameHistoryData, isSearched]);

  const handleSearch = async (e) => {
    e.preventDefault();
    if (!gameName.trim()) return;

    setLoading(true);
    setError(null);

    try {
      console.log('=== 플레이어 프로필 조회 시작 ===');
      console.log('GameName:', gameName);
      console.log('TagLine:', tagLine);

      // 1. 플레이어 프로필 조회 (계정 + 랭크 + 소환사 정보)
      const profileData = await getPlayerProfile(gameName.trim(), tagLine);
      setPlayerProfile(profileData);

      console.log('=== 플레이어 프로필 조회 완료 ===');
      console.log('Profile Data:', profileData);

      // 2. 게임 기록 조회
      console.log('=== 게임 기록 조회 시작 ===');
      const historyData = await getGameHistory(gameName.trim(), tagLine, 20);
      setGameHistoryData(historyData);

      console.log('=== 게임 기록 조회 완료 ===');
      console.log('Game History Count:', historyData.length);

      setIsSearched(true);
    } catch (err) {
      console.error('검색 실패:', err);
      setError(err.response?.data?.message || '플레이어를 찾을 수 없습니다.');
      setPlayerProfile(null);
      setGameHistoryData([]);

      // 에러 발생 시 저장된 상태 제거
      localStorage.removeItem('lolSearchState');
    } finally {
      setLoading(false);
    }
  };

  // 새로운 검색 시작 (상태 초기화)
  const handleNewSearch = () => {
    setPlayerProfile(null);
    setGameHistoryData([]);
    setIsSearched(false);
    setError(null);
    setGameName('');
    setTagLine('KR1');
    localStorage.removeItem('lolSearchState');
  };

  return (
      <>
        <GlobalStyle />
        <Container>
          <SearchContainer isSearched={isSearched}>
            <Title isSearched={isSearched}>롤 전적검색</Title>
            <SearchForm onSubmit={handleSearch}>
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
              {isSearched && (
                  <SearchButton
                      type="button"
                      onClick={handleNewSearch}
                      style={{ marginLeft: '10px', background: '#6b7280' }}
                  >
                    새 검색
                  </SearchButton>
              )}
            </SearchForm>
          </SearchContainer>

          {loading && <LoadingSpinner />}
          {error && <ErrorMessage>{error}</ErrorMessage>}

          {playerProfile && (
              <>
                {/* 플레이어 정보 (아이콘 + 레벨 포함) */}
                <UserInfo playerProfile={playerProfile} />

                {/* 랭크 정보 */}
                <RankInfo leagueEntries={playerProfile.leagueEntries} />

                {/* 게임 기록 */}
                {gameHistoryData.length > 0 && (
                    <GameHistory gameHistory={gameHistoryData} />
                )}
              </>
          )}
        </Container>
      </>
  );
}

export default App;
