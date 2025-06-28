package com.example.farmerapp.controllers;


import com.example.farmerapp.models.UserProfile;
import com.example.farmerapp.models.User;
import com.example.farmerapp.models.UserDTO;
import com.example.farmerapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();

    }
    @GetMapping("/exists/{id}")
    public ResponseEntity<?> exists(@PathVariable String id)
        {
            return ResponseEntity.ok(userService.exists(id));
        }


    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile() {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> user = userService.getUserById(userId);
        if (user.isPresent()) {
            User u = user.get();
            UserProfile profile = new UserProfile(u.getId(), u.getName(),
                    String.valueOf(u.getDob()), u.getAddress(), u.getPhoneNumber());
            return ResponseEntity.ok(profile);
        } else {
            return ResponseEntity.status(404).body("User not found");
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

    @PutMapping("/update")
    public ResponseEntity<User> updateUser(

            @RequestBody UserDTO updatedUser) {


        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        User updated = userService.updateUser(userId, updatedUser);

        return ResponseEntity.ok(updated);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
