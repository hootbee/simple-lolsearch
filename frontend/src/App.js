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
import GameHistory from './components/GameHistory';
import { searchSummoner, getGameHistory } from './services/api';

function App() {
  const [gameName, setGameName] = useState('');
  const [tagLine, setTagLine] = useState('KR1');
  const [account, setAccount] = useState(null);
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
      // 계정 정보 조회
      const accountData = await searchSummoner(gameName.trim(), tagLine);
      setAccount(accountData);

      // 게임 기록 조회
      const historyData = await getGameHistory(gameName.trim(), tagLine, 20);
      setGameHistoryData(historyData);

      setIsSearched(true);
    } catch (err) {
      console.error('검색 실패:', err);
      setError(err.response?.data?.message || '소환사를 찾을 수 없습니다.');
      setAccount(null);
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

          {account && <UserInfo account={account} />}
          {gameHistoryData.length > 0 && <GameHistory gameHistory={gameHistoryData} />}
        </Container>
      </>
  );
}

export default App;
