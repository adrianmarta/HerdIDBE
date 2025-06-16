import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './MainScreen.css';
import logo from '../assets/logo.png';
import PropTypes from 'prop-types';

function MainScreen({ setAuth }) {
  const navigate = useNavigate();
  const [salePosts, setSalePosts] = useState([]);
  const [favorites, setFavorites] = useState([]); // Store favorite salePost IDs
  const [priceRange, setPriceRange] = useState({ min: '', max: '' });
  const [locationFilter, setLocationFilter] = useState('');
  const [speciesFilter, setSpeciesFilter] = useState('');
  const [availableSpecies, setAvailableSpecies] = useState([]);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    fetchFavorites();
    fetchAvailableSpecies();
  }, []);

  // Fetch filtered sale posts
  const fetchFilteredPosts = async () => {
    const token = localStorage.getItem('jwt');
    if (!token) return;

    setIsLoading(true);
    try {
      const filter = {
        minPrice: priceRange.min ? Number(priceRange.min) : null,
        maxPrice: priceRange.max ? Number(priceRange.max) : null,
        location: locationFilter || null,
        species: speciesFilter || null,
      };

      const response = await axios.post(
        'http://localhost:8080/api/sale-posts/filter',
        filter,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      setSalePosts(response.data);
    } catch (error) {
      console.error('Error fetching filtered posts:', error);
    } finally {
      setIsLoading(false);
    }
  };

  // Fetch available species
  const fetchAvailableSpecies = async () => {
    const token = localStorage.getItem('jwt');
    if (!token) return;
    try {
      const response = await axios.get(
        'http://localhost:8080/api/animals/species',
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      setAvailableSpecies(response.data);
    } catch (error) {
      console.error('Error fetching species:', error);
    }
  };

  // Fetch user's favorites
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
      setFavorites(response.data.map((fav) => fav.salePost.id));
    } catch (error) {
      console.error('Error fetching favorites:', error);
    }
  };

  // Apply filters when any filter changes
  useEffect(() => {
    const timeoutId = setTimeout(() => {
      fetchFilteredPosts();
    }, 300); // Debounce filter changes

    return () => clearTimeout(timeoutId);
  }, [priceRange, locationFilter, speciesFilter]);

  const handlePriceChange = (type, value) => {
    // Only allow numbers and empty string
    if (value === '' || /^\d*$/.test(value)) {
      setPriceRange((prev) => ({
        ...prev,
        [type]: value,
      }));
    }
  };

  const handleLocationChange = (value) => {
    setLocationFilter(value);
  };

  const handleSpeciesChange = (value) => {
    setSpeciesFilter(value);
  };

  // Toggle favorite for a sale post
  const toggleFavorite = async (postId, isFav) => {
    const token = localStorage.getItem('jwt');
    if (!token) return;
    try {
      if (isFav) {
        await axios.delete(
          `http://localhost:8080/api/sale-posts/${postId}/favorite`,
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );
      } else {
        await axios.post(
          `http://localhost:8080/api/sale-posts/${postId}/favorite`,
          {},
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );
      }
      fetchFavorites();
    } catch (error) {
      console.error('Error toggling favorite:', error);
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
            {/* Heart SVG */}
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
            {/* Profile SVG */}
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
          <button className="main-logout-btn alt" onClick={handleLogout}>
            Logout
          </button>
        </div>
      </header>

      {/* Sidebar (Filtering) */}
      <div className="main-content-wrapper">
        <nav className="main-drawer open">
          <div className="main-drawer-buttons">
            <div className="price-filter-container">
              <h3 className="filter-title">Price Range (€)</h3>
              <div className="price-inputs">
                <input
                  type="text"
                  placeholder="Min"
                  value={priceRange.min}
                  onChange={(e) => handlePriceChange('min', e.target.value)}
                  className="price-input"
                />
                <span className="price-separator">-</span>
                <input
                  type="text"
                  placeholder="Max"
                  value={priceRange.max}
                  onChange={(e) => handlePriceChange('max', e.target.value)}
                  className="price-input"
                />
              </div>
            </div>

            <div className="location-filter-container">
              <h3 className="filter-title">Location</h3>
              <input
                type="text"
                placeholder="Enter location..."
                value={locationFilter}
                onChange={(e) => handleLocationChange(e.target.value)}
                className="location-input"
              />
            </div>

            <div className="species-filter-container">
              <h3 className="filter-title">Species</h3>
              <select
                value={speciesFilter}
                onChange={(e) => handleSpeciesChange(e.target.value)}
                className="species-select"
              >
                <option value="">All Species</option>
                {availableSpecies.map((species) => (
                  <option key={species.value} value={species.value}>
                    {species.label}
                  </option>
                ))}
              </select>
            </div>
          </div>
        </nav>

        {/* Sale Posts */}
        <main className="main-content">
          {isLoading ? (
            <div className="loading-spinner">Loading...</div>
          ) : (
            <div className="sale-posts-grid">
              {salePosts.length === 0 ? (
                <p>No sale posts available.</p>
              ) : (
                salePosts.map((post) => {
                  const isFavorite = favorites.includes(post.id);
                  return (
                    <div key={post.id}>
                      <div
                        className="sale-post-card"
                        onClick={() => navigate(`/sale-posts/${post.id}`)}
                      >
                        {/* Heart Button */}
                        <button
                          className="sale-post-heart-btn"
                          title={
                            isFavorite
                              ? 'Remove from favorites'
                              : 'Add to favorites'
                          }
                          onClick={(e) => {
                            e.stopPropagation();
                            toggleFavorite(post.id, isFavorite);
                          }}
                        >
                          <svg
                            width="24"
                            height="24"
                            viewBox="0 0 24 24"
                            fill={isFavorite ? '#ff4d4f' : 'none'}
                            stroke="#ff4d4f"
                            strokeWidth="2"
                            strokeLinecap="round"
                            strokeLinejoin="round"
                          >
                            <path d="M20.8 5.6a5.5 5.5 0 0 0-7.8 0l-.5.5-.5-.5a5.5 5.5 0 0 0-7.8 7.8l.5.5L12 21.3l7.3-7.3.5-.5a5.5 5.5 0 0 0 0-7.8z"></path>
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
                      {/* Price, Number of Animals, and Interest Button below the card */}
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
          )}
        </main>
      </div>
    </div>
  );
}

MainScreen.propTypes = {
  setAuth: PropTypes.func.isRequired,
};

export default MainScreen;
