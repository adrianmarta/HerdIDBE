package com.example.farmerapp.controllers;

import com.example.farmerapp.JwtUtil;
import com.example.farmerapp.models.User;
import com.example.farmerapp.repositories.UserRepository;

import lombok.AllArgsConstructor;
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
    private UserRepository userService;

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOpt = userService.findByIdAndEmail(
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
        return ResponseEntity.ok(new LoginResponse(token));
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (userService.existsById(req.getId())) {
            return ResponseEntity.badRequest().body("ID already exists.");
        }
        User user = new User();
        user.setId(req.getId());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setName(req.getName());
        user.setDob(req.getDob());
        user.setAddress(req.getAddress());
        user.setPhoneNumber(req.getPhoneNumber());
        userService.save(user);
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
    @Getter
    @AllArgsConstructor
    public static class LoginResponse {
        private String token;
    }

}
