import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import './HerdDetails.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faMars, faVenus, faTrash } from '@fortawesome/free-solid-svg-icons'; // Icons
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css'; // Import styles

function HerdDetails() {
  const { herdId } = useParams();
  const navigate = useNavigate();
  const [animals, setAnimals] = useState([]);
  const [selectedAnimals, setSelectedAnimals] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchAnimals();
  }, []);

  const fetchAnimals = async () => {
    const token = localStorage.getItem('jwt');
    if (!token) {
      setError('Unauthorized. Please log in.');
      return;
    }

    try {
      const response = await axios.get(
        `http://localhost:8080/api/folders/${herdId}/animals`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      setAnimals(response.data);
    } catch (err) {
      setError('Failed to fetch animals.');
    }
  };

  const toggleAnimalSelection = (animalId) => {
    setSelectedAnimals((prev) =>
      prev.includes(animalId)
        ? prev.filter((id) => id !== animalId)
        : [...prev, animalId]
    );
  };

  const deleteFromFolder = async () => {
    if (selectedAnimals.length === 0) return;
    const token = localStorage.getItem('jwt');
    if (!token) return;

    try {
      await axios.put(
        `http://localhost:8080/api/folders/${herdId}/remove-animals`,
        selectedAnimals,
        { headers: { Authorization: `Bearer ${token}` } }
      );

      setAnimals(
        animals.filter((animal) => !selectedAnimals.includes(animal.id))
      );
      setSelectedAnimals([]);
      toast.success('✅ Removed from herd!', { position: 'top-center' });
    } catch (error) {
      toast.error('❌ Failed to remove from herd.');
    }
  };

  const deleteGlobally = async () => {
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
      toast.warn('🚨 Animals deleted globally!', { position: 'top-center' });
    } catch (error) {
      toast.error('❌ Failed to delete animals globally.');
    }
  };

  return (
    <div className="herd-details-container">
      <ToastContainer /> {/* Toastify Container */}
      {/* Header */}
      <header className="herd-header">
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
      <div className="herd-content">
        <h2>Herd Animals</h2>
        {error && <p className="error-message">{error}</p>}

        {selectedAnimals.length > 0 && (
          <div className="buttons-container">
            <button className="delete-folder-btn" onClick={deleteFromFolder}>
              Remove from Herd
            </button>
            <button className="delete-global-btn" onClick={deleteGlobally}>
              <FontAwesomeIcon icon={faTrash} /> Delete Globally
            </button>
          </div>
        )}

        {/* Animals Grid (3 Columns) */}
        <div className="animals-grid">
          {animals.length > 0 ? (
            animals.map((animal) => (
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
                    <FontAwesomeIcon
                      icon={faMars}
                      className="gender-icon male"
                    />
                  )}

                  <span className="animal-dob">{animal.birthDate}</span>
                </div>
              </div>
            ))
          ) : (
            <p>No animals found in this herd.</p>
          )}
        </div>
      </div>
    </div>
  );
}

export default HerdDetails;
