import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css'; // Import Toastify styles
import './SalePostCreate.css';

function SalePostCreate() {
  const navigate = useNavigate();
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [price, setPrice] = useState('');
  const [herds, setHerds] = useState([]);
  const [expiryDate, setExpiryDate] = useState('');
  const [selectedHerd, setSelectedHerd] = useState(null);
  const [animals, setAnimals] = useState([]);
  const [selectedAnimals, setSelectedAnimals] = useState([]);
  const [showPopup, setShowPopup] = useState(false);
  const [images, setImages] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchHerds();
    fetchAllAnimals();
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

  const fetchAllAnimals = async () => {
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
      setError('Error fetching all animals.');
    }
  };

  const fetchAnimalsInHerd = async (herdId) => {
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
      setError('Error fetching animals from herd.');
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
      setSelectedAnimals([]);
    } else {
      setSelectedAnimals(animals.map((animal) => animal.id));
    }
  };

  const handleImageUpload = (event) => {
    const files = Array.from(event.target.files); // ✅ Convert FileList to Array
    setImages(files);
  };

  const confirmAnimalSelection = () => {
    setShowPopup(false);
    toast.success('✅ Animals added to sale post.');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const token = localStorage.getItem('jwt');
    if (!token) return toast.error('❌ Unauthorized!');

    const formData = new FormData();
    formData.append('title', title);
    formData.append('description', description);
    formData.append('price', price);
    formData.append('expiryDate', expiryDate);
    // Append all selected animals
    selectedAnimals.forEach((id) => formData.append('animals', id));

    // ✅ Append all images correctly
    images.forEach((image) => formData.append('images', image));

    try {
      await axios.post('http://localhost:8080/api/sale-posts', formData, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      toast.success('✅ Sale post created successfully!');
      navigate('/main');
    } catch (error) {
      console.error(error);
      toast.error('❌ Error creating sale post.');
    }
  };

  return (
    <div className="sale-post-container">
      <ToastContainer /> {/* Toastify Container */}
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
          <input
            type="datetime-local"
            placeholder="Expiry Date & Time"
            value={expiryDate}
            onChange={(e) => setExpiryDate(e.target.value)}
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

          {/* Image Upload */}
          <input
            type="file"
            multiple
            accept="image/*"
            onChange={handleImageUpload}
          />

          {/* Image Previews */}
          <div className="sale-post-images-preview">
            {images.length > 0 && (
              <>
                <h3>Selected Images</h3>
                <div className="sale-post-images-grid">
                  {images.map((image, index) => (
                    <img
                      key={index}
                      src={URL.createObjectURL(image)}
                      alt={`Preview ${index}`}
                      className="sale-post-image-preview"
                    />
                  ))}
                </div>
              </>
            )}
          </div>

          <button className="sale-post-submit" type="submit">
            Create Sale Post
          </button>
        </form>
      </div>
      {/* Animal Selection Popup */}
      {showPopup && (
        <div className="sale-post-popup">
          <div className="sale-post-popup-content">
            <h3>Select Herd or "Registru"</h3>
            <div className="sale-post-herds">
              <button key="registru" onClick={fetchAllAnimals}>
                Registru (All Animals)
              </button>
              {herds.map((herd) => (
                <button
                  key={herd.id}
                  onClick={() => fetchAnimalsInHerd(herd.id)}
                >
                  {herd.name}
                </button>
              ))}
            </div>

            {/* Show Animals */}
            {animals.length > 0 && (
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

export default SalePostCreate;
