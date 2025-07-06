import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
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
import { getPlayerProfile, getGameHistory } from '../services/api';

function HomePage() {
    const [gameName, setGameName] = useState('');
    const [tagLine, setTagLine] = useState('KR1');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    const handleSearch = async (e) => {
        e.preventDefault();
        if (!gameName.trim()) return;

        setLoading(true);
        setError(null);

        try {
            // 검색 성공 시 결과 페이지로 이동
            navigate(`/search/${encodeURIComponent(gameName.trim())}/${tagLine}`);
        } catch (err) {
            setError('검색 중 오류가 발생했습니다.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <>
            <GlobalStyle />
            <Container>
                <SearchContainer isSearched={false}>
                    <Title isSearched={false}>롤 전적검색</Title>
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
            </Container>
        </>
    );
}

export default HomePage;
