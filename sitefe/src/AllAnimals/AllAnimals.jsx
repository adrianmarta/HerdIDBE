import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './AllAnimals.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faMars, faVenus } from '@fortawesome/free-solid-svg-icons'; // Male & Female icons

function AllAnimals() {
  const navigate = useNavigate();
  const [animals, setAnimals] = useState([]);
  const [selectedAnimals, setSelectedAnimals] = useState([]);
  const [herds, setHerds] = useState([]);
  const [selectedHerd, setSelectedHerd] = useState(null);
  const [error, setError] = useState('');
  const [showHerdPopup, setShowHerdPopup] = useState(false);

  useEffect(() => {
    fetchAnimals();
    fetchHerds();
  }, []);

  const fetchAnimals = async () => {
    const token = localStorage.getItem('jwt');
    if (!token) return;

    try {
      const response = await axios.get(
        'http://localhost:8080/api/animals/owner',
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      setAnimals(response.data);
    } catch (error) {
      setError('Failed to load animals.');
    }
  };

  const fetchHerds = async () => {
    const token = localStorage.getItem('jwt');
    if (!token) return;

    try {
      const response = await axios.get(
        'http://localhost:8080/api/folders/user',
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      setHerds(response.data);
    } catch (error) {
      setError('Failed to load herds.');
    }
  };

  const toggleAnimalSelection = (animalId) => {
    setSelectedAnimals((prev) =>
      prev.includes(animalId)
        ? prev.filter((id) => id !== animalId)
        : [...prev, animalId]
    );
  };

  const deleteSelectedAnimals = async () => {
    if (selectedAnimals.length === 0) return;

    const token = localStorage.getItem('jwt');
    if (!token) return;

    try {
      await axios.delete(
        `http://localhost:8080/api/animals/delete?animalIds=${selectedAnimals.join(
          ','
        )}`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      setAnimals(
        animals.filter((animal) => !selectedAnimals.includes(animal.id))
      );
      setSelectedAnimals([]);
    } catch (error) {
      setError('Failed to delete selected animals.');
    }
  };

  const addToHerd = async () => {
    if (!selectedHerd || selectedAnimals.length === 0) return;

    const token = localStorage.getItem('jwt');
    if (!token) return;

    try {
      await axios.put(
        `http://localhost:8080/api/folders/${selectedHerd}/add-animals`,
        selectedAnimals,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      setSelectedAnimals([]);
      setShowHerdPopup(false);
    } catch (error) {
      setError('Failed to add animals to herd.');
    }
  };

  return (
    <div className="animals-container">
      {/* Header */}
      <header className="animals-header">
        <h1 className="logo" onClick={() => navigate('/main')}>
          Farmer App
        </h1>
        <button
          className="logout-btn"
          onClick={() => {
            localStorage.removeItem('jwt');
            navigate('/');
          }}
        >
          Logout
        </button>
      </header>

      {/* Content */}
      <div className="animals-content">
        <h2>All Animals</h2>
        {error && <p className="error-message">{error}</p>}

        {/* Buttons (Only Show When Animals Are Selected) */}
        {selectedAnimals.length > 0 && (
          <div className="buttons-container">
            <button className="delete-btn" onClick={deleteSelectedAnimals}>
              Delete
            </button>
            <button
              className="add-to-herd-btn"
              onClick={() => setShowHerdPopup(true)}
            >
              Add to Herd
            </button>
          </div>
        )}

        {/* Animals Grid (3 Columns) */}
        <div className="animals-grid">
          {animals.map((animal) => (
            <div
              key={animal.id}
              className={`animal-card ${
                selectedAnimals.includes(animal.id) ? 'selected' : ''
              }`}
              onClick={() => toggleAnimalSelection(animal.id)}
            >
              <div className="animal-info">
                <span>{animal.id}</span>

                {/* Gender Icon */}
                {animal.gender === 'Female' ? (
                  <FontAwesomeIcon
                    icon={faVenus}
                    className="gender-icon female"
                  />
                ) : (
                  <FontAwesomeIcon icon={faMars} className="gender-icon male" />
                )}

                <span className="animal-dob">{animal.birthDate}</span>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Herd Selection Popup */}
      {showHerdPopup && (
        <div className="popup-overlay">
          <div className="popup">
            <h3>Select a Herd</h3>
            {herds.map((herd) => (
              <button
                key={herd.id}
                className={`herd-button ${
                  selectedHerd === herd.id ? 'selected' : ''
                }`}
                onClick={() => setSelectedHerd(herd.id)}
              >
                {herd.name}
              </button>
            ))}
            <button className="confirm-btn" onClick={addToHerd}>
              Confirm
            </button>
            <button
              className="cancel-btn"
              onClick={() => setShowHerdPopup(false)}
            >
              Cancel
            </button>
          </div>
        </div>
      )}
    </div>
  );
}

export default AllAnimals;
