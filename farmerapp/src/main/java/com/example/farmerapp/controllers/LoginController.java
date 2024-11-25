package com.example.farmerapp.controllers;

import com.example.farmerapp.JwtUtil;
import com.example.farmerapp.models.User;
import com.example.farmerapp.repositories.UserRepository;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        // Validate the ID and CNP
        Optional<User> user = userRepository.findById(loginRequest.getId());

        if (user.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        // Assuming the CNP is stored in the user model
        if (user.get().getCnp().equals(loginRequest.getCnp())) {
            // Generate JWT token
            String token = jwtUtil.generateToken(user.get().getId());

            return ResponseEntity.ok(Map.of("token", token));  // Send token as response
        }

        return ResponseEntity.status(401).body("Invalid CNP");
    }

    @Getter
    public static class LoginRequest {
        // Getters and Setters
        private String id;
        private String cnp;

        public void setId(String id) {
            this.id = id;
        }

        public void setCnp(String cnp) {
            this.cnp = cnp;
        }
    }
}
