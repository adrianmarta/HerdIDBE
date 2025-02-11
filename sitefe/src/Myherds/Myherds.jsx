import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './MyHerds.css';

function MyHerds() {
  const [herds, setHerds] = useState([]);
  const [selectedHerds, setSelectedHerds] = useState(new Set());
  const [error, setError] = useState('');
  const [showPopup, setShowPopup] = useState(false);
  const [newFolderName, setNewFolderName] = useState('');
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

  const handleAddFolder = async () => {
    if (!newFolderName.trim()) return;
    const token = localStorage.getItem('jwt');

    try {
      const response = await axios.post(
        'http://localhost:8080/api/folders',
        { name: newFolderName },
        { headers: { Authorization: `Bearer ${token}` } }
      );

      setHerds([...herds, response.data]);
      setShowPopup(false);
      setNewFolderName('');
    } catch (err) {
      setError('Failed to create folder.');
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
          <button className="add-folder-btn" onClick={() => setShowPopup(true)}>
            Add Folder
          </button>
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
            <div
              key={herd.id}
              className={`herd-card ${
                selectedHerds.has(herd.id) ? 'selected' : ''
              }`}
              onClick={() => handleToggleSelect(herd.id)}
            >
              <h3>{herd.name}</h3>
              <p>Click to view animals</p>
            </div>
          ))}
        </div>
      </div>

      {/* Add Folder Popup */}
      {showPopup && (
        <div className="popup-overlay">
          <div className="popup">
            <h3>Create New Folder</h3>
            <input
              type="text"
              className="popup-input"
              value={newFolderName}
              onChange={(e) => setNewFolderName(e.target.value)}
              placeholder="Enter folder name"
            />
            <div className="popup-buttons">
              <button className="confirm-btn" onClick={handleAddFolder}>
                Create
              </button>
              <button
                className="cancel-btn"
                onClick={() => setShowPopup(false)}
              >
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default MyHerds;
