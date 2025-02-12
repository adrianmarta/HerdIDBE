import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './SalePostCreate.css';

function SalePost() {
  const navigate = useNavigate();
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [price, setPrice] = useState('');
  const [herds, setHerds] = useState([]);
  const [selectedHerd, setSelectedHerd] = useState(null);
  const [animals, setAnimals] = useState([]);
  const [selectedAnimals, setSelectedAnimals] = useState([]);
  const [showPopup, setShowPopup] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchHerds();
  }, []);

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
      setError('Error fetching herds.');
    }
  };

  const fetchAnimals = async (herdId) => {
    const token = localStorage.getItem('jwt');
    if (!token) return;

    try {
      const response = await axios.get(
        `http://localhost:8080/api/folders/${herdId}/animals`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      setAnimals(response.data);
      setSelectedHerd(herdId);
    } catch (error) {
      setError('Error fetching animals.');
    }
  };

  const toggleAnimalSelection = (animalId) => {
    setSelectedAnimals((prev) =>
      prev.includes(animalId)
        ? prev.filter((id) => id !== animalId)
        : [...prev, animalId]
    );
  };

  const selectAllAnimals = () => {
    if (selectedAnimals.length === animals.length) {
      setSelectedAnimals([]); // Deselect all if already selected
    } else {
      setSelectedAnimals(animals.map((animal) => animal.id));
    }
  };

  const confirmAnimalSelection = () => {
    setShowPopup(false); // Close popup
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const token = localStorage.getItem('jwt');
    if (!token) return;

    const salePostData = {
      title,
      description,
      price,
      animals: selectedAnimals.map((id) => id.toString()),
    };

    try {
      await axios.post('http://localhost:8080/api/sale-posts', salePostData, {
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      });
      navigate('/main');
    } catch (error) {
      setError('Error creating sale post.');
    }
  };

  return (
    <div className="sale-post-container">
      {/* Header */}
      <header className="sale-post-header">
        <h1 className="sale-post-logo" onClick={() => navigate('/main')}>
          Farmer App
        </h1>
        <button
          className="sale-post-logout-btn"
          onClick={() => {
            localStorage.removeItem('jwt');
            navigate('/');
          }}
        >
          Logout
        </button>
      </header>

      {/* Sale Post Form */}
      <div className="sale-post-form-container">
        <h2>Create Sale Post</h2>
        {error && <p className="sale-post-error">{error}</p>}

        <form className="sale-post-form" onSubmit={handleSubmit}>
          <input
            type="text"
            placeholder="Title"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            required
          />
          <textarea
            placeholder="Description"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            required
          />
          <input
            type="number"
            placeholder="Price"
            value={price}
            onChange={(e) => setPrice(e.target.value)}
            required
          />

          {/* Add Animals Button */}
          <button
            type="button"
            className="sale-post-add-animals"
            onClick={() => setShowPopup(true)}
          >
            Add Animals
          </button>

          {/* Show Selected Animals */}
          {selectedAnimals.length > 0 && (
            <p>Selected Animals: {selectedAnimals.length}</p>
          )}

          <button className="sale-post-submit" type="submit">
            Create Sale Post
          </button>
        </form>
      </div>

      {/* Animal Selection Popup */}
      {showPopup && (
        <div className="sale-post-popup">
          <div className="sale-post-popup-content">
            <h3>Select Herd</h3>
            <div className="sale-post-herds">
              {herds.map((herd) => (
                <button key={herd.id} onClick={() => fetchAnimals(herd.id)}>
                  {herd.name}
                </button>
              ))}
            </div>

            {/* Show Animals if Herd is Selected */}
            {selectedHerd && (
              <>
                <h3>Select Animals</h3>
                <button
                  className="sale-post-select-all"
                  onClick={selectAllAnimals}
                >
                  {selectedAnimals.length === animals.length
                    ? 'Deselect All'
                    : 'Select All'}
                </button>
                <div className="sale-post-animals">
                  {animals.map((animal) => (
                    <div
                      key={animal.id}
                      className={
                        selectedAnimals.includes(animal.id) ? 'selected' : ''
                      }
                      onClick={() => toggleAnimalSelection(animal.id)}
                    >
                      {animal.id} - {animal.gender}
                    </div>
                  ))}
                </div>
                <button
                  className="sale-post-confirm"
                  onClick={confirmAnimalSelection}
                >
                  Confirm
                </button>
              </>
            )}
            <button
              className="sale-post-close"
              onClick={() => setShowPopup(false)}
            >
              Close
            </button>
          </div>
        </div>
      )}
    </div>
  );
}

export default SalePost;
