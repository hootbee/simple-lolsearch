import styled, { createGlobalStyle } from 'styled-components';

export const GlobalStyle = createGlobalStyle`
  * {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
  }

  body {
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    min-height: 100vh;
    color: #333;
  }
`;

export const Container = styled.div`
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
`;

export const SearchContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: ${props => props.isSearched ? 'flex-start' : 'center'};
  min-height: ${props => props.isSearched ? 'auto' : '100vh'};
  transition: all 0.3s ease;
  margin-bottom: ${props => props.isSearched ? '40px' : '0'};
`;

export const Title = styled.h1`
  font-size: ${props => props.isSearched ? '2rem' : '3rem'};
  color: white;
  margin-bottom: 30px;
  text-shadow: 2px 2px 4px rgba(0,0,0,0.3);
  transition: all 0.3s ease;
`;

export const SearchForm = styled.form`
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
`;

export const SearchInput = styled.input`
  padding: 12px 16px;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  width: 200px;
  box-shadow: 0 4px 6px rgba(0,0,0,0.1);
  
  &:focus {
    outline: none;
    box-shadow: 0 4px 12px rgba(0,0,0,0.2);
  }
`;

export const TagInput = styled.input`
  padding: 12px 16px;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  width: 80px;
  box-shadow: 0 4px 6px rgba(0,0,0,0.1);
  
  &:focus {
    outline: none;
    box-shadow: 0 4px 12px rgba(0,0,0,0.2);
  }
`;

export const SearchButton = styled.button`
  padding: 12px 24px;
  background: #ff6b6b;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
  box-shadow: 0 4px 6px rgba(0,0,0,0.1);
  transition: all 0.2s ease;
  
  &:hover {
    background: #ff5252;
    transform: translateY(-2px);
    box-shadow: 0 6px 12px rgba(0,0,0,0.2);
  }
  
  &:disabled {
    background: #ccc;
    cursor: not-allowed;
    transform: none;
  }
`;

export const LoadingSpinner = styled.div`
  border: 4px solid #f3f3f3;
  border-top: 4px solid #3498db;
  border-radius: 50%;
  width: 50px;
  height: 50px;
  animation: spin 1s linear infinite;
  margin: 20px auto;
  
  @keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
  }
`;

export const ErrorMessage = styled.div`
  background: #f44336;
  color: white;
  padding: 12px 16px;
  border-radius: 8px;
  margin: 20px 0;
  text-align: center;
`;
