import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import './MyBids'; // Reuse the existing styles

function MyBids() {
  const [bids, setBids] = useState([]);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    fetchUserBids();
  }, []);

  const fetchUserBids = async () => {
    const token = localStorage.getItem('jwt');
    if (!token) return;

    try {
      const response = await axios.get('http://localhost:8080/api/bids/user', {
        headers: { Authorization: `Bearer ${token}` },
      });
      setBids(response.data);
    } catch (error) {
      setError('Failed to load your bids.');
    }
  };

  return (
    <div className="main-container">
      <header className="main-header">
        <h1 className="main-app-title" onClick={() => navigate('/main')}>
          Farmer App
        </h1>
        <button className="main-logout-btn" onClick={() => navigate('/')}>
          Logout
        </button>
      </header>

      <main className="main-content">
        <h2>My Bids</h2>
        {error && <p className="main-error">{error}</p>}
        {bids.length === 0 ? (
          <p>You have not placed any bids yet.</p>
        ) : (
          <div className="sale-posts-grid">
            {bids.map((bid) => (
              <div
                key={bid.id}
                className="sale-post-card"
                onClick={() => navigate(`/sale-posts/${bid.salePost.id}`)}
              >
                {/* Show Sale Post Image */}
                {bid.salePost.images && bid.salePost.images.length > 0 ? (
                  <img
                    src={`data:image/png;base64,${bid.salePost.images[0]}`}
                    alt="Sale Post"
                    className="sale-post-image"
                  />
                ) : (
                  <div className="sale-post-placeholder">No Image</div>
                )}

                {/* Sale Post and Bid Details */}
                <div className="sale-post-content">
                  <h3>{bid.salePost.title}</h3>
                  <p>{bid.salePost.description}</p>
                  <p>
                    <strong>Price:</strong> ${bid.salePost.price}
                  </p>
                  <p>
                    <strong>Your Bid:</strong> ${bid.bidAmount}
                  </p>
                </div>
              </div>
            ))}
          </div>
        )}
      </main>
    </div>
  );
}

export default MyBids;
