import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './MyPosts.css';

function MyPosts() {
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchPosts = async () => {
      try {
        const token = localStorage.getItem('jwt');
        if (!token) {
          navigate('/');
          return;
        }

        const response = await axios.get(
          'http://localhost:8080/api/sale-posts/user/posts',
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );

        setPosts(response.data);
        setLoading(false);
      } catch (err) {
        setError('Failed to fetch your posts. Please try again later.');
        setLoading(false);
      }
    };

    fetchPosts();
  }, [navigate]);

  const handlePostClick = (postId) => {
    navigate(`/sale-posts/${postId}`);
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('ro-RO', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  };

  if (loading) {
    return <div className="my-posts-loading">Loading your posts...</div>;
  }

  if (error) {
    return <div className="my-posts-error">{error}</div>;
  }

  return (
    <div className="my-posts-container">
      <h1>My Sale Posts</h1>
      {posts.length === 0 ? (
        <div className="my-posts-empty">
          <p>You haven't created any sale posts yet.</p>
          <button
            className="create-post-button"
            onClick={() => navigate('/create-sale-post')}
          >
            Create Your First Post
          </button>
        </div>
      ) : (
        <div className="my-posts-grid">
          {posts.map((post) => (
            <div
              key={post.id}
              className="my-post-card"
              onClick={() => handlePostClick(post.id)}
            >
              {post.images && post.images.length > 0 ? (
                <img
                  src={`data:image/jpeg;base64,${post.images[0]}`}
                  alt={post.title}
                  className="my-post-image"
                />
              ) : (
                <div className="my-post-no-image">No Image</div>
              )}
              <div className="my-post-details">
                <h2>{post.title}</h2>
                <p className="my-post-price">{post.price} RON</p>
                <p className="my-post-animals">
                  {post.numberOfAnimals}{' '}
                  {post.numberOfAnimals === 1 ? 'animal' : 'animals'}
                </p>
                <p className="my-post-date">
                  Posted on {formatDate(post.creationDate)}
                </p>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default MyPosts;
