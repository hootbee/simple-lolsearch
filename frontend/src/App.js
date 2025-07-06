import React, { useState } from 'react';
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
import RankInfo from './components/RankInfo';  // default export로 import
import GameHistory from './components/GameHistory';
import { getPlayerProfile, getGameHistory } from './services/api';

// 나머지 코드는 동일...


function App() {
  const [gameName, setGameName] = useState('');
  const [tagLine, setTagLine] = useState('KR1');
  const [playerProfile, setPlayerProfile] = useState(null);
  const [gameHistoryData, setGameHistoryData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [isSearched, setIsSearched] = useState(false);

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
    } finally {
      setLoading(false);
    }
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
