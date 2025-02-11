package com.example.farmerapp.services;




import com.example.farmerapp.models.User;
import com.example.farmerapp.models.UserDTO;
import com.example.farmerapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(String id, UserDTO userDTO) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Update only the allowed fields
            user.setName(userDTO.getName());
            user.setDob(userDTO.getDob());
            user.setAddress(userDTO.getAddress());
            user.setPhoneNumber(userDTO.getPhoneNumber());

            return userRepository.save(user);
        } else {
            throw new IllegalArgumentException("User not found for update.");
        }
    }


    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }
}

