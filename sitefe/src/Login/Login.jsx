import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './Login.css';

function Login({ setAuth }) {
  const [id, setId] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  // React Router navigation hook
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const response = await axios.post(
        'http://localhost:8080/api/auth/login',
        { id, email, password },
        { headers: { 'Content-Type': 'application/json' } }
      );

      const { token } = response.data;
      if (token) {
        localStorage.setItem('jwt', token);
        setAuth(true); // Update authentication state
        navigate('/main');
      } else {
        setError('No token found in response.');
      }
    } catch (err) {
      if (err.response?.data) {
        setError(err.response.data); // e.g. "User not found" or "Invalid password"
      } else {
        setError(err.message);
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <div className="login-box">
        <h2 className="login-header">Login</h2>
        <form onSubmit={handleLogin}>
          <div className="form-group">
            <label htmlFor="id">ID</label>
            <input
              type="text"
              id="id"
              value={id}
              onChange={(e) => setId(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="email">Email</label>
            <input
              type="email"
              id="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
              type="password"
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          {error && <p className="login-error">{error}</p>}

          <button type="submit" className="login-button" disabled={loading}>
            {loading ? 'Logging in...' : 'Login'}
          </button>

          <p className="register-link">
            Don't have an account? <span onClick={() => navigate('/register')}>Register here</span>
          </p>
        </form>
      </div>
    </div>
  );
}

export default Login;
