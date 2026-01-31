import { Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import { ThemeProvider } from './contexts/ThemeContext';
import { useAnalytics } from './hooks/useAnalytics';
import Header from './components/common/Header';
import Navigation from './components/common/Navigation';
import HomePage from './pages/HomePage';
import TeamsPage from './pages/TeamsPage';
import StandingsPage from './pages/StandingsPage';
import TeamDetailPage from './pages/TeamDetailPage';
import PlayersPage from './pages/PlayersPage';
import PlayerDetailPage from './pages/PlayerDetailPage';
import GamesPage from './pages/GamesPage';
import GameDetailPage from './pages/GameDetailPage';
import LoginPage from './pages/LoginPage';
import LandingPage from './pages/LandingPage';
import AdminPage from './pages/AdminPage';
import AccountPage from './pages/AccountPage';
import LeaderboardsPage from './pages/LeaderboardsPage';
import ComparePage from './pages/ComparePage';

function AppContent() {
  const { isAuthenticated, isLoading } = useAuth();
  useAnalytics();

  if (isLoading) {
    return (
      <div className="loading-screen">
        <p>Loading...</p>
      </div>
    );
  }

  if (!isAuthenticated) {
    return (
      <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    );
  }

  return (
    <div className="app">
      <Header />
      <Navigation />
      <main className="main-content">
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/teams" element={<TeamsPage />} />
          <Route path="/teams/:id" element={<TeamDetailPage />} />
          <Route path="/standings" element={<StandingsPage />} />
          <Route path="/leaderboards" element={<LeaderboardsPage />} />
          <Route path="/compare" element={<ComparePage />} />
          <Route path="/players" element={<PlayersPage />} />
          <Route path="/players/:id" element={<PlayerDetailPage />} />
          <Route path="/games" element={<GamesPage />} />
          <Route path="/games/:id" element={<GameDetailPage />} />
          <Route path="/account" element={<AccountPage />} />
          <Route path="/admin" element={<AdminPage />} />
          <Route path="/login" element={<Navigate to="/" replace />} />
        </Routes>
      </main>
    </div>
  );
}

function App() {
  return (
    <ThemeProvider>
      <AuthProvider>
        <AppContent />
      </AuthProvider>
    </ThemeProvider>
  );
}

export default App;
