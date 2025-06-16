import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import './SalePostDetails.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faClock,
  faUser,
  faLocationDot,
  faPhone,
} from '@fortawesome/free-solid-svg-icons';
import logo from '../assets/logo.png';

function SalePostDetails() {
  const { postId } = useParams();
  const navigate = useNavigate();
  const [post, setPost] = useState(null);
  const [error, setError] = useState('');
  const [currentImageIndex, setCurrentImageIndex] = useState(0);
  const [isInterested, setIsInterested] = useState(false);
  const [isFavorite, setIsFavorite] = useState(false);

  useEffect(() => {
    fetchSalePost();
    checkInterest();
    checkFavorite();
  }, []);

  const fetchSalePost = async () => {
    try {
      const response = await axios.get(
        `http://localhost:8080/api/sale-posts/${postId}/details`
      );
      console.log('Received sale post data:', response.data);
      setPost(response.data);
    } catch (error) {
      setError('Error fetching sale post details.');
      console.error('Error fetching sale post:', error);
    }
  };

  const checkInterest = async () => {
    const token = localStorage.getItem('jwt');
    if (!token) return;
    try {
      const response = await axios.get(
        'http://localhost:8080/api/sale-posts/interested',
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      setIsInterested(
        response.data.some((interest) => interest.salePost.id === postId)
      );
    } catch (error) {
      console.error('Error checking interest:', error);
    }
  };

  const markInterest = async () => {
    const token = localStorage.getItem('jwt');
    if (!token) return;
    try {
      await axios.post(
        `http://localhost:8080/api/sale-posts/${postId}/interest`,
        {},
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      setIsInterested(true);
    } catch {
      console.error('Error registering interest');
    }
  };

  const handlePrevImage = () => {
    setCurrentImageIndex((prev) =>
      prev === 0 ? post.images.length - 1 : prev - 1
    );
  };

  const handleNextImage = () => {
    setCurrentImageIndex((prev) =>
      prev === post.images.length - 1 ? 0 : prev + 1
    );
  };

  const checkFavorite = async () => {
    const token = localStorage.getItem('jwt');
    if (!token) return;
    try {
      const response = await axios.get(
        'http://localhost:8080/api/sale-posts/user/favorites',
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      setIsFavorite(response.data.some((fav) => fav.salePost.id === postId));
    } catch {
      // Error handling can be added later
    }
  };

  const toggleFavorite = async () => {
    const token = localStorage.getItem('jwt');
    if (!token) return;
    try {
      if (isFavorite) {
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
      checkFavorite();
    } catch {
      // Error handling can be added later
    }
  };

  return (
    <div className="main-container">
      {/* Header matching MainScreen exactly */}
      <header className="main-header">
        <div className="main-header-left">
          <img
            src={logo}
            alt="Farmer App Logo"
            className="main-logo circular-logo"
          />
          <h1 className="main-app-title" onClick={() => navigate('/main')}>
            Farmer App
          </h1>
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

      {/* Centered Main Content */}
      <div className="main-content-wrapper details-centered">
        <div className="sale-post-details-wrapper">
          {/* Favorite Heart Button */}
          <button
            className="sale-post-heart-btn details-heart"
            title={isFavorite ? 'Remove from favorites' : 'Add to favorites'}
            onClick={toggleFavorite}
            style={{ position: 'absolute', top: 24, right: 24, zIndex: 2 }}
          >
            <svg
              width="28"
              height="28"
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
          {error && <p className="error-message">{error}</p>}
          {post ? (
            <>
              {/* Image Section */}
              <div className="sale-post-details-images">
                {post.images && post.images.length > 0 ? (
                  <>
                    <button
                      className="image-nav-btn left"
                      onClick={handlePrevImage}
                    >
                      &#8249;
                    </button>
                    <img
                      src={`data:image/jpeg;base64,${post.images[currentImageIndex]}`}
                      alt={`Sale post ${currentImageIndex + 1}`}
                      className="sale-post-details-main-image"
                    />
                    <button
                      className="image-nav-btn right"
                      onClick={handleNextImage}
                    >
                      &#8250;
                    </button>
                    {post.images.length > 1 && (
                      <div className="image-counter">
                        {currentImageIndex + 1} / {post.images.length}
                      </div>
                    )}
                  </>
                ) : (
                  <div className="sale-post-placeholder">
                    No images available
                  </div>
                )}
              </div>

              {/* Details Section */}
              <div className="sale-post-details-content">
                <h2 className="sale-post-details-title">{post.title}</h2>

                <div className="sale-post-details-price-section">
                  <span className="sale-post-details-price">
                    ${post.price.toLocaleString()}
                  </span>
                  <span className="sale-post-details-animals">
                    {console.log('Number of animals:', post.numberOfAnimals)}
                    {post.numberOfAnimals || 0}{' '}
                    {post.numberOfAnimals === 1 ? 'Animal' : 'Animals'}
                  </span>
                </div>

                {/* Species Information */}
                {post.species && post.species.length > 0 && (
                  <div className="sale-post-details-species">
                    <h3>Species</h3>
                    <div className="species-list">
                      {post.species.map((species, index) => (
                        <span key={index} className="species-tag">
                          {species}
                        </span>
                      ))}
                    </div>
                  </div>
                )}

                <div className="sale-post-details-description">
                  <h3>Description</h3>
                  <p>{post.description}</p>
                </div>

                <div className="sale-post-details-meta">
                  <div className="sale-post-details-date">
                    <FontAwesomeIcon icon={faClock} className="meta-icon" />
                    <span>
                      Posted on{' '}
                      {new Date(post.creationDate).toLocaleString('en-GB', {
                        day: '2-digit',
                        month: 'short',
                        year: 'numeric',
                        hour: '2-digit',
                        minute: '2-digit',
                        hour12: false,
                      })}
                    </span>
                  </div>
                </div>

                {/* Owner Details */}
                <div className="sale-post-details-owner">
                  <h3>Owner Information</h3>
                  <div className="owner-info">
                    <div className="owner-detail">
                      <FontAwesomeIcon icon={faUser} className="meta-icon" />
                      <span>{post.ownerName || 'Anonymous'}</span>
                    </div>
                    {post.location && (
                      <div className="owner-detail">
                        <FontAwesomeIcon
                          icon={faLocationDot}
                          className="meta-icon"
                        />
                        <span>{post.location}</span>
                      </div>
                    )}
                    {post.phone && (
                      <div className="owner-detail">
                        <FontAwesomeIcon icon={faPhone} className="meta-icon" />
                        <span>{post.phone}</span>
                      </div>
                    )}
                  </div>
                </div>

                {/* Interest Button */}
                <button
                  className={`interest-btn${isInterested ? ' interested' : ''}`}
                  onClick={markInterest}
                  disabled={isInterested}
                >
                  {isInterested ? 'Interested' : 'I am interested'}
                </button>
              </div>
            </>
          ) : (
            <div className="loading-state">Loading...</div>
          )}
        </div>
      </div>
    </div>
  );
}

export default SalePostDetails;
