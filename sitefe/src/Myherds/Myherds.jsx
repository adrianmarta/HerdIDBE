import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './MyHerds.css';

function MyHerds() {
  const [herds, setHerds] = useState([]);
  const [selectedHerds, setSelectedHerds] = useState(new Set());
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    fetchHerds();
  }, []);

  const fetchHerds = async () => {
    const token = localStorage.getItem('jwt');
    if (!token) {
      setError('Unauthorized. Please log in.');
      return;
    }

    try {
      const response = await axios.get(
        'http://localhost:8080/api/folders/user',
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      setHerds(response.data);
    } catch (err) {
      setError('Failed to fetch herds.');
    }
  };

  const handleToggleSelect = (herdId) => {
    setSelectedHerds((prevSelected) => {
      const updated = new Set(prevSelected);
      if (updated.has(herdId)) {
        updated.delete(herdId);
      } else {
        updated.add(herdId);
      }
      return updated;
    });
  };

  const handleDeleteSelected = async () => {
    const token = localStorage.getItem('jwt');
    if (!token || selectedHerds.size === 0) return;

    try {
      await Promise.all(
        [...selectedHerds].map((herdId) =>
          axios.delete(`http://localhost:8080/api/folders/${herdId}`, {
            headers: { Authorization: `Bearer ${token}` },
          })
        )
      );

      setHerds(herds.filter((herd) => !selectedHerds.has(herd.id)));
      setSelectedHerds(new Set());
    } catch (err) {
      setError('Failed to delete selected herds.');
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('jwt');
    navigate('/');
  };

  return (
    <div className="herds-container">
      {/* Header */}
      <header className="herds-header">
        <h1 className="logo" onClick={() => navigate('/main')}>
          Farmer App
        </h1>
        <button className="logout-btn" onClick={handleLogout}>
          Logout
        </button>
      </header>

      {/* Herds Page Content */}
      <div className="herds-content">
        <h2>My Herds</h2>
        {error && <p className="error-message">{error}</p>}

        <div className="buttons-container">
          <button
            className="registru-btn"
            onClick={() => navigate('/all-animals')}
          >
            Registru
          </button>
          <button className="add-folder-btn">Add Folder</button>
          <button
            className="delete-btn"
            onClick={handleDeleteSelected}
            disabled={selectedHerds.size === 0}
          >
            Delete Selected
          </button>
        </div>

        {/* Herds List */}
        <div className="herds-list">
          {herds.map((herd) => (
            <div key={herd.id} className="herd-card">
              {/* Checkbox for selection */}
              <input
                type="checkbox"
                className="herd-checkbox"
                checked={selectedHerds.has(herd.id)}
                onChange={() => handleToggleSelect(herd.id)}
                onClick={(e) => e.stopPropagation()} // Prevent navigation when clicking checkbox
              />

              {/* Clickable herd name for navigation */}
              <h3 onClick={() => navigate(`/herd/${herd.id}`)}>{herd.name}</h3>
              <p>Click to view animals</p>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

export default MyHerds;
