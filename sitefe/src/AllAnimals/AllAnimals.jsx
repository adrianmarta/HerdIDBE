import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css'; // Import styles
import './AllAnimals.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faMars, faVenus, faTrash } from '@fortawesome/free-solid-svg-icons'; // Trash icon for delete

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
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setAnimals(response.data);
    } catch (error) {
      toast.error('❌ Failed to load animals.');
    }
  };

  const fetchHerds = async () => {
    const token = localStorage.getItem('jwt');
    if (!token) return;

    try {
      const response = await axios.get(
        'http://localhost:8080/api/folders/user',
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setHerds(response.data);
    } catch (error) {
      toast.error('❌ Failed to load herds.');
    }
  };

  const toggleAnimalSelection = (animalId) => {
    setSelectedAnimals((prev) =>
      prev.includes(animalId)
        ? prev.filter((id) => id !== animalId)
        : [...prev, animalId]
    );
  };

  const addToHerd = async () => {
    if (!selectedHerd || selectedAnimals.length === 0) return;

    const token = localStorage.getItem('jwt');
    if (!token) return;

    try {
      const response = await axios.put(
        `http://localhost:8080/api/folders/${selectedHerd}/add-animals`,
        selectedAnimals,
        { headers: { Authorization: `Bearer ${token}` } }
      );

      toast.success('✅ Animals successfully added to the herd.');
      setSelectedAnimals([]);
      setShowHerdPopup(false);
    } catch (error) {
      if (error.response && error.response.status === 409) {
        toast.error(
          `❌ The following animals are already in the folder: ${error.response.data}`
        );
      } else {
        toast.error('⚠ Something went wrong while adding animals to the herd.');
      }
    }
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
        { headers: { Authorization: `Bearer ${token}` } }
      );

      setAnimals(
        animals.filter((animal) => !selectedAnimals.includes(animal.id))
      );
      setSelectedAnimals([]);
      toast.success('🗑 Selected animals have been deleted.');
    } catch (error) {
      toast.error('❌ Failed to delete selected animals.');
    }
  };

  return (
    <div className="animals-container">
      <ToastContainer
        position="top-right"
        autoClose={3000}
        hideProgressBar
        closeOnClick
        pauseOnHover
        draggable
      />

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

      <div className="animals-content">
        <h2>All Animals</h2>

        {selectedAnimals.length > 0 && (
          <div className="buttons-container">
            <button className="delete-btn" onClick={deleteSelectedAnimals}>
              <FontAwesomeIcon icon={faTrash} /> Delete
            </button>
            <button
              className="add-to-herd-btn"
              onClick={() => setShowHerdPopup(true)}
            >
              Add to Herd
            </button>
          </div>
        )}

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
