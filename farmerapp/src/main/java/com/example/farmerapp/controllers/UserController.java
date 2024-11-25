package com.example.farmerapp.controllers;


import com.example.farmerapp.JwtUtil;
import com.example.farmerapp.UserProfile;
import com.example.farmerapp.models.User;
import com.example.farmerapp.repositories.UserRepository;
import com.example.farmerapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();

    }
    @GetMapping(value = "/profile", produces = "application/json")
    public ResponseEntity<?> getUserProfile(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");  // Remove "Bearer " prefix

        if (jwtUtil.validateToken(token)) {
            Optional<User> user = userService.getUserById(jwtUtil.extractUserId(token));

            if (user.isPresent()) {

                User u = user.get();

                UserProfile profile = new UserProfile(u.getId(), u.getName(), String.valueOf(u.getDob()), u.getAddress(),u.getPhoneNumber());

                return ResponseEntity.ok(profile);
            }
            else{
                return ResponseEntity.status(404).body("User not found");
            }
        } else {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public User createUser( @RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
