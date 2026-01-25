import { Link } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import { useTheme } from '../../contexts/ThemeContext';
import './Header.css';

function Header() {
  const { user, logout } = useAuth();
  const { theme, toggleTheme } = useTheme();

  return (
    <header className="header">
      <Link to="/" style={{ color: 'white', textDecoration: 'none' }}>
        <h1>MLB Stats</h1>
      </Link>
      <div className="header-right">
        <button onClick={toggleTheme} className="theme-toggle" aria-label="Toggle theme">
          {theme === 'light' ? '🌙' : '☀️'}
        </button>
        {user && (
          <div className="user-menu">
            {user.picture && (
              <img src={user.picture} alt={user.name} className="user-avatar" />
            )}
            <span className="user-name">{user.name}</span>
            <button onClick={logout} className="logout-btn">
              Logout
            </button>
          </div>
        )}
      </div>
    </header>
  );
}

export default Header;
