import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import logo from '../assets/logo.png';
import '../MainScreen/MainScreen.css';

function MyFavorites() {
  const navigate = useNavigate();
  const [favorites, setFavorites] = useState([]);

  useEffect(() => {
    fetchFavorites();
  }, []);

  const fetchFavorites = async () => {
    const token = localStorage.getItem('jwt');
    if (!token) return;
    try {
      const response = await axios.get(
        'http://localhost:8080/api/sale-posts/user/favorites',
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      setFavorites(response.data);
    } catch {
      // Error handling can be added later
    }
  };

  const unfavorite = async (postId) => {
    const token = localStorage.getItem('jwt');
    if (!token) return;
    try {
      await axios.delete(
        `http://localhost:8080/api/sale-posts/${postId}/favorite`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      setFavorites(favorites.filter((fav) => fav.salePost.id !== postId));
    } catch {
      // Error handling can be added later
    }
  };

  return (
    <div className="main-container">
      <header className="main-header">
        <div className="main-header-left">
          <img
            src={logo}
            alt="Farmer App Logo"
            className="main-logo circular-logo"
          />
        </div>
        <div className="main-header-right">
          <button
            className="main-header-btn"
            title="Favorites"
            onClick={() => navigate('/my-favorites')}
          >
            <svg
              width="28"
              height="28"
              viewBox="0 0 24 24"
              fill="#1557ff"
              stroke="#1557ff"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
            >
              <path d="M20.8 5.6a5.5 5.5 0 0 0-7.8 0l-.5.5-.5-.5a5.5 5.5 0 0 0-7.8 7.8l.5.5L12 21.3l7.3-7.3.5-.5a5.5 5.5 0 0 0 0-7.8z"></path>
            </svg>
          </button>
          <button
            className="main-header-btn"
            onClick={() => navigate('/create-sale-post')}
            title="Sell"
          >
            Sell
          </button>
          <button
            className="main-header-btn"
            onClick={() => navigate('/my-posts')}
            title="My Posts"
          >
            My Posts
          </button>
          <button
            className="main-header-btn"
            onClick={() => navigate('/profile')}
            title="Profile"
          >
            <svg
              width="28"
              height="28"
              viewBox="0 0 24 24"
              fill="none"
              stroke="#1557ff"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
            >
              <circle cx="12" cy="8" r="4" />
              <path d="M2 20c0-4 8-6 10-6s10 2 10 6" />
            </svg>
          </button>
          <button
            className="main-logout-btn alt"
            onClick={() => {
              localStorage.removeItem('jwt');
              navigate('/');
            }}
          >
            Logout
          </button>
        </div>
      </header>
      <div className="main-content-wrapper">
        <main className="main-content">
          <div className="sale-posts-grid">
            {favorites.length === 0 ? (
              <p>You have no favorites yet.</p>
            ) : (
              favorites.map((fav) => {
                const post = fav.salePost;
                return (
                  <div key={post.id}>
                    <div
                      className="sale-post-card"
                      onClick={() => navigate(`/sale-posts/${post.id}`)}
                    >
                      {/* Heart Button (filled, allows unfavorite) */}
                      <button
                        className="sale-post-heart-btn"
                        onClick={(e) => {
                          e.stopPropagation();
                          unfavorite(post.id);
                        }}
                        title="Unfavorite"
                      >
                        <svg
                          width="24"
                          height="24"
                          viewBox="0 0 24 24"
                          fill="#1557ff"
                          stroke="#1557ff"
                          strokeWidth="2"
                          strokeLinecap="round"
                          strokeLinejoin="round"
                        >
                          <path d="M12 21s-6.2-5.2-8.2-7.2A5.5 5.5 0 0 1 12 5.5a5.5 5.5 0 0 1 8.2 8.3C18.2 15.8 12 21 12 21z" />
                        </svg>
                      </button>
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
                    </div>
                    {/* Price and Number of Animals below the card */}
                    <div className="sale-post-content sale-post-minimal">
                      <div className="sale-post-price">€{post.price}</div>
                      <div className="sale-post-animals">
                        {post.numberOfAnimals} animals
                      </div>
                    </div>
                  </div>
                );
              })
            )}
          </div>
        </main>
      </div>
    </div>
  );
}

export default MyFavorites;
