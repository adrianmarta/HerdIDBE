import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import './SalePostDetails.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faClock } from '@fortawesome/free-solid-svg-icons';

function SalePostDetails() {
  const { postId } = useParams();
  const navigate = useNavigate();
  const [post, setPost] = useState(null);
  const [error, setError] = useState('');
  const [currentImageIndex, setCurrentImageIndex] = useState(0);
  const [showOfferPopup, setShowOfferPopup] = useState(false);
  const [offerPrice, setOfferPrice] = useState('');

  useEffect(() => {
    fetchSalePost();
  }, []);

  const fetchSalePost = async () => {
    try {
      const response = await axios.get(
        `http://localhost:8080/api/sale-posts/${postId}/details`
      );
      setPost(response.data);
    } catch (err) {
      setError('Error fetching sale post details.');
      toast.error('❌ Could not load sale post.');
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

  const handleMakeOffer = async () => {
    if (!offerPrice || offerPrice < post.price) {
      toast.error(`❌ Offer must be at least $${post.price}`);
      return;
    }

    const token = localStorage.getItem('jwt');
    if (!token) {
      toast.error('❌ Please log in to place a bid.');
      return;
    }
    const requestBody = {
      salePostID: postId, // ✅ Ensure correct field name
      bidAmount: offerPrice,
    };

    console.log('🛠 Sending request body:', requestBody); // ✅ Debugging log

    try {
      const response = await axios.post(
        'http://localhost:8080/api/bids',
        requestBody,
        {
          headers: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json',
          },
        }
      );

      toast.success('✅ Offer placed successfully!');
      setShowOfferPopup(false);
    } catch (error) {
      console.error('❌ Bid error:', error);
      toast.error(`❌ ${error.response?.data || 'Failed to place the bid.'}`);
    }
  };

  return (
    <div className="sale-post-details-container">
      <ToastContainer /> {/* Toast Notifications */}
      {/* Header */}
      <header className="sale-post-details-header">
        <h1
          className="sale-post-details-logo"
          onClick={() => navigate('/main')}
        >
          Farmer App
        </h1>
        <button
          className="sale-post-details-back-btn"
          onClick={() => navigate('/main')}
        >
          Back
        </button>
      </header>
      {/* Main Content */}
      <div className="sale-post-details-wrapper">
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
                    alt={`Sale post ${currentImageIndex}`}
                  />
                  <button
                    className="image-nav-btn right"
                    onClick={handleNextImage}
                  >
                    &#8250;
                  </button>
                </>
              ) : (
                <p>No images available.</p>
              )}
            </div>

            {/* Details Section */}
            <div className="sale-post-details-content">
              <h2>{post.title}</h2>
              <p className="sale-post-details-description">
                {post.description}
              </p>

              <p className="sale-post-details-price">💰 Price: ${post.price}</p>
              <p className="sale-post-details-animals">
                🐄 Animals: {post.numberOfAnimals}
              </p>
              <p className="sale-post-details-expiry">
                <FontAwesomeIcon icon={faClock} className="clock-icon" />
                Expiry Date:{' '}
                {new Date(post.expiryDate).toLocaleString('en-GB', {
                  day: '2-digit',
                  month: 'short',
                  year: 'numeric',
                  hour: '2-digit',
                  minute: '2-digit',
                  hour12: false,
                })}
                {post.isSold ? (
                  <>
                    🎉 <strong>Winner:</strong> {post.winnerBid.bidder.name} - $
                    {post.winnerBid.bidAmount}
                    <br />
                    <a href={`/payment/${post.id}`} className="pay-link">
                      💳 Complete Payment
                    </a>
                  </>
                ) : (
                  <>
                    ⏳ <strong>Expiry Date:</strong>{' '}
                    {new Date(post.expiryDate).toLocaleString()}
                  </>
                )}
              </p>
              {/* Owner Details */}
              <div className="sale-post-details-owner">
                <h3>Owner Details</h3>
                <p>👤 {post.ownerName}</p>
                <p>📍 {post.location}</p>
              </div>

              {/* Make Offer Button */}
              <button
                className="sale-post-details-make-offer-btn"
                onClick={() => setShowOfferPopup(true)}
              >
                Make Offer
              </button>
            </div>
          </>
        ) : (
          <p>Loading...</p>
        )}
      </div>
      {/* Offer Popup */}
      {showOfferPopup && (
        <div className="sale-post-details-popup">
          <div className="sale-post-details-popup-content">
            <h3>Make an Offer</h3>
            <input
              type="number"
              min={post.price}
              placeholder={`Minimum: $${post.price}`}
              value={offerPrice}
              onChange={(e) => setOfferPrice(e.target.value)}
            />
            <button className="confirm-offer-btn" onClick={handleMakeOffer}>
              Confirm
            </button>
            <button
              className="close-popup-btn"
              onClick={() => setShowOfferPopup(false)}
            >
              Cancel
            </button>
          </div>
        </div>
      )}
    </div>
  );
}

export default SalePostDetails;
