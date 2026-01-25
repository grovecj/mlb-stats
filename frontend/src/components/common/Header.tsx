import { Link } from 'react-router-dom';

function Header() {
  return (
    <header className="header">
      <Link to="/" style={{ color: 'white', textDecoration: 'none' }}>
        <h1>MLB Stats</h1>
      </Link>
    </header>
  );
}

export default Header;
