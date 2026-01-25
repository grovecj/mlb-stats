import { NavLink } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';

function Navigation() {
  const { isAdmin } = useAuth();

  return (
    <nav className="navigation">
      <ul>
        <li>
          <NavLink to="/" className={({ isActive }) => isActive ? 'active' : ''}>
            Home
          </NavLink>
        </li>
        <li>
          <NavLink to="/teams" className={({ isActive }) => isActive ? 'active' : ''}>
            Teams
          </NavLink>
        </li>
        <li>
          <NavLink to="/players" className={({ isActive }) => isActive ? 'active' : ''}>
            Players
          </NavLink>
        </li>
        <li>
          <NavLink to="/games" className={({ isActive }) => isActive ? 'active' : ''}>
            Games
          </NavLink>
        </li>
        {isAdmin && (
          <li>
            <NavLink to="/admin" className={({ isActive }) => isActive ? 'active' : ''}>
              Admin
            </NavLink>
          </li>
        )}
      </ul>
    </nav>
  );
}

export default Navigation;
