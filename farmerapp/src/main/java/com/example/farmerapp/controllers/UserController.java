package com.example.farmerapp.controllers;


import com.example.farmerapp.JwtUtil;
import com.example.farmerapp.SalePostDetails;
import com.example.farmerapp.UserProfile;
import com.example.farmerapp.models.SalePost;
import com.example.farmerapp.models.User;
import com.example.farmerapp.models.UserDTO;
import com.example.farmerapp.repositories.UserRepository;
import com.example.farmerapp.services.SalePostService;
import com.example.farmerapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private SalePostService salePostService;

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
    @GetMapping("/{id}/details")
    public ResponseEntity<?> getSalePostWithOwnerDetails(@PathVariable String id) {
        Optional<SalePost> salePostOptional = salePostService.getSalePostById(id);

        if (salePostOptional.isPresent()) {
            SalePost salePost = salePostOptional.get();
            User owner = salePost.getOwner(); // Get the user who owns the post

            // Fetch owner's profile using their ID
            Optional<User> ownerOptional = userService.getUserById(owner.getId());

            if (ownerOptional.isEmpty()) {
                return ResponseEntity.status(404).body("Owner not found");
            }

            User ownerDetails = ownerOptional.get();

            // Construct response with sale post + owner details
            return ResponseEntity.ok(new SalePostDetails(
                    salePost.getTitle(),
                    salePost.getDescription(),
                    salePost.getPrice(),
                    salePost.getAnimals().size(),
                    salePost.getImages(),
                    ownerDetails.getName(),
                    ownerDetails.getPhoneNumber(),
                    ownerDetails.getAddress()
            ));
        }
        return ResponseEntity.status(404).body("Sale post not found");
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
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UserDTO updatedUser) {

        // Extract token and remove "Bearer " prefix
        String token = authHeader.replace("Bearer ", "");

        // Validate token
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body(null); // Unauthorized
        }

        // Extract user ID from token
        String userId = jwtUtil.extractUserId(token);

        // Update user with the extracted ID
        User updated = userService.updateUser(userId, updatedUser);

        return ResponseEntity.ok(updated);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
