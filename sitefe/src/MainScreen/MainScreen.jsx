import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './MainScreen.css';

function MainScreen({ setAuth }) {
  const navigate = useNavigate();
  const [isDrawerOpen, setIsDrawerOpen] = useState(false);

  const handleLogout = () => {
    localStorage.removeItem('jwt'); // Remove token
    setAuth(false); // Update authentication state
    navigate('/');
  };

  return (
    <div className="main-container">
      <header className="header">
        <div className="header-left">
          <button
            className="menu-button"
            onClick={() => setIsDrawerOpen(!isDrawerOpen)}
          >
            ☰
          </button>
          <h1 className="app-title">Farmer App</h1>
        </div>

        <div className="header-right">
          <button className="logout-btn" onClick={handleLogout}>
            Logout
          </button>
        </div>
      </header>

      <div className="content-wrapper">
        <nav className={`drawer ${isDrawerOpen ? 'open' : 'closed'}`}>
          <div className="drawer-buttons">
            <button className="drawer-btn" onClick={() => navigate('/herds')}>
              My Herds
            </button>
            <button className="drawer-btn" onClick={() => navigate('/profile')}>
              Profile
            </button>
          </div>
        </nav>

        <main className="main-content">
          <h2>Welcome to the Main Screen</h2>
          <p>Here you can see your herds, profile, etc.</p>
        </main>
      </div>
    </div>
  );
}

export default MainScreen;
