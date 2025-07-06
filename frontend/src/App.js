import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import HomePage from './pages/HomePage';
import SearchResultPage from './pages/SearchResultPage';

function App() {
  return (
      <Router>
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/search/:gameName/:tagLine" element={<SearchResultPage />} />
        </Routes>
      </Router>
  );
}

export default App;
