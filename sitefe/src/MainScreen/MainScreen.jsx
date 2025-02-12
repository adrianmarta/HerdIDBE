import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './MainScreen.css';

function MainScreen({ setAuth }) {
  const navigate = useNavigate();
  const [isDrawerOpen, setIsDrawerOpen] = useState(false);
  const [salePosts, setSalePosts] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchSalePosts();
  }, []);

  // ✅ Fetch sale posts from other users
  const fetchSalePosts = async () => {
    const token = localStorage.getItem('jwt');
    if (!token) return;

    try {
      const response = await axios.get(
        'http://localhost:8080/api/sale-posts/others',
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      setSalePosts(response.data);
    } catch (error) {
      setError('Failed to load sale posts.');
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('jwt');
    setAuth(false);
    navigate('/');
  };

  return (
    <div className="main-container">
      {/* Header */}
      <header className="main-header">
        <div className="main-header-left">
          <button
            className="main-menu-button"
            onClick={() => setIsDrawerOpen(!isDrawerOpen)}
          >
            ☰
          </button>
          <h1 className="main-app-title">Farmer App</h1>
        </div>

        <div className="main-header-right">
          <button className="main-logout-btn" onClick={handleLogout}>
            Logout
          </button>
        </div>
      </header>

      {/* Sidebar */}
      <div className="main-content-wrapper">
        <nav className={`main-drawer ${isDrawerOpen ? 'open' : 'closed'}`}>
          <div className="main-drawer-buttons">
            <button
              className="main-drawer-btn"
              onClick={() => navigate('/herds')}
            >
              My Herds
            </button>
            <button
              className="main-drawer-btn"
              onClick={() => navigate('/profile')}
            >
              Profile
            </button>
            <button
              className="main-drawer-btn"
              onClick={() => navigate('/create-sale-post')}
            >
              Create Sale Post
            </button>
          </div>
        </nav>

        {/* Sale Posts */}
        <main className="main-content">
          <h2>Latest Sale Posts</h2>
          {error && <p className="main-error">{error}</p>}

          <div className="sale-posts-grid">
            {salePosts.length === 0 ? (
              <p>No sale posts available.</p>
            ) : (
              salePosts.map((post) => (
                <div
                  key={post.id}
                  className="sale-post-card"
                  onClick={() => navigate(`/sale-post/${post.id}`)}
                >
                  {/* Show Image if Available */}
                  {post.images && post.images.length > 0 ? (
                    <img
                      src={`data:image/png;base64,${post.images[0]}`}
                      alt="Sale Post"
                      className="sale-post-image"
                    />
                  ) : (
                    <div className="sale-post-placeholder">No Image</div>
                  )}

                  {/* Post Details */}
                  <div className="sale-post-content">
                    <h3>{post.title}</h3>
                    <p>{post.description}</p>
                    <p>
                      <strong>Price:</strong> ${post.price}
                    </p>
                    <p className="sale-post-owner">
                      Posted by: {post.owner.name}
                    </p>
                  </div>
                </div>
              ))
            )}
          </div>
        </main>
      </div>
    </div>
  );
}

export default MainScreen;
