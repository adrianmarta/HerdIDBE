import React, { useEffect, useState } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Login from './Login/Login';
import MainScreen from './MainScreen/MainScreen';
import Profile from './Profile/Profile.jsx';
import MyHerds from './Myherds/Myherds.jsx';
import HerdDetails from './HerdDetails/HerdDetails.jsx';
import SalePost from './SellPostCreate/SalePostCreate.jsx';
import AllAnimals from './AllAnimals/AllAnimals.jsx';

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem('jwt');
    if (token) {
      setIsAuthenticated(true);
    }
  }, []);
  return (
    <BrowserRouter>
      <Routes>
        <Route
          path="/"
          element={
            isAuthenticated ? (
              <Navigate to="/main" />
            ) : (
              <Login setAuth={setIsAuthenticated} />
            )
          }
        />
        <Route
          path="/main"
          element={
            isAuthenticated ? (
              <MainScreen setAuth={setIsAuthenticated} />
            ) : (
              <Navigate to="/" />
            )
          }
        />
        <Route
          path="/profile"
          element={isAuthenticated ? <Profile /> : <Navigate to="/" />}
        />
        <Route
          path="/herds"
          element={isAuthenticated ? <MyHerds /> : <Navigate to="/" />}
        />
        <Route
          path="/herd/:herdId"
          element={isAuthenticated ? <HerdDetails /> : <Navigate to="/" />}
        />
        <Route
          path="/create-sale-post"
          element={isAuthenticated ? <SalePost /> : <Navigate to="/" />}
        />
        <Route
          path="/all-animals"
          element={isAuthenticated ? <AllAnimals /> : <Navigate to="/" />}
        />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
