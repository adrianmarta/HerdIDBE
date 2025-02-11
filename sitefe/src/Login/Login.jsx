import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './Login.css';

function Login({ setAuth }) {
  const [id, setId] = useState('');
  const [cnp, setCnp] = useState('');
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
        { id, cnp },
        { headers: { 'Content-Type': 'application/json' } }
      );

      const { token } = response.data;
      if (token) {
        localStorage.setItem('jwt', token);
        setAuth(true); // Update authentication stat
        navigate('/main');
      } else {
        setError('No token found in response.');
      }
    } catch (err) {
      if (err.response?.data) {
        setError(err.response.data); // e.g. "User not found" or "Invalid CNP"
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
            <label htmlFor="cnp">CNP</label>
            <input
              type="password"
              id="cnp"
              value={cnp}
              onChange={(e) => setCnp(e.target.value)}
              required
            />
          </div>

          {error && <p className="login-error">{error}</p>}

          <button type="submit" className="login-button" disabled={loading}>
            {loading ? 'Logging in...' : 'Login'}
          </button>
        </form>
      </div>
    </div>
  );
}

export default Login;
