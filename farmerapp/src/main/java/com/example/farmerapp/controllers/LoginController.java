package com.example.farmerapp.controllers;

import com.example.farmerapp.JwtUtil;
import com.example.farmerapp.models.User;
import com.example.farmerapp.repositories.UserRepository;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
@CrossOrigin(origins = "*") // <--- Add this
@RestController
@RequestMapping("/api/auth")
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOpt = userRepository.findByIdAndEmail(
                loginRequest.getId(), loginRequest.getEmail()
        );

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getId());
        return ResponseEntity.ok(Map.of("token", token));
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (userRepository.existsById(req.getId())) {
            return ResponseEntity.badRequest().body("ID already exists.");
        }

        if (userRepository.existsByEmail(req.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists.");
        }

        User user = new User();
        user.setId(req.getId());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setName(req.getName());
        user.setDob(req.getDob());
        user.setAddress(req.getAddress());
        user.setPhoneNumber(req.getPhoneNumber());

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully.");
    }

    @Getter
    @Setter
    public static class LoginRequest {
        private String id;
        private String email;
        private String password;

    }
    @Setter
    @Getter
    public static class RegisterRequest {
        private String id;
        private String email;
        private String password;
        private String name;
        private LocalDate dob;
        private String address;
        private String phoneNumber;
    }
}
