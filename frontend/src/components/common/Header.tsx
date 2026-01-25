import { Link } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import './Header.css';

function Header() {
  const { user, logout } = useAuth();

  return (
    <header className="header">
      <Link to="/" style={{ color: 'white', textDecoration: 'none' }}>
        <h1>MLB Stats</h1>
      </Link>
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
    </header>
  );
}

export default Header;
