import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import './HerdDetails.css';

function HerdDetails() {
  const { herdId } = useParams(); // Get herd ID from URL
  const [animals, setAnimals] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
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

    fetchAnimals();
  }, [herdId]);

  return (
    <div className="herd-details-page">
      <h2>Herd Animals</h2>
      {error && <p className="error-message">{error}</p>}
      <div className="animals-list">
        {animals.length > 0 ? (
          <ul>
            {animals.map((animal) => (
              <li key={animal.id}>
                <strong>ID:</strong> {animal.id} | <strong>Gender:</strong>{' '}
                {animal.gender} |<strong>Birth Date:</strong> {animal.birthDate}
              </li>
            ))}
          </ul>
        ) : (
          <p>No animals found in this herd.</p>
        )}
      </div>
    </div>
  );
}

export default HerdDetails;
