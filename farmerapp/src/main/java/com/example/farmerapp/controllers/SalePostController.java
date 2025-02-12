package com.example.farmerapp.controllers;

import com.example.farmerapp.JwtUtil;
import com.example.farmerapp.models.Animal;
import com.example.farmerapp.models.SalePost;
import com.example.farmerapp.models.SalePostRequest;
import com.example.farmerapp.models.User;
import com.example.farmerapp.services.AnimalService;
import com.example.farmerapp.services.SalePostService;
import com.example.farmerapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/sale-posts")
public class SalePostController {

    @Autowired
    private SalePostService salePostService;
    @Autowired
    private AnimalService animalService;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;
    // Get all sale posts
    @GetMapping
    public List<SalePost> getAllSalePosts() {
        return salePostService.getAllSalePosts();
    }

    // Get a sale post by ID
    @GetMapping("/{id}")
    public ResponseEntity<SalePost> getSalePostById(@PathVariable String id) {
        Optional<SalePost> salePost = salePostService.getSalePostById(id);
        return salePost.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Create a sale post
    @PostMapping
    public ResponseEntity<?> createSalePost(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody SalePostRequest salePostRequest) {

        String token = authHeader.replace("Bearer ", ""); // Remove "Bearer " prefix

        if (!jwtUtil.validateToken(token)) {
            System.out.println("token");
            return ResponseEntity.status(401).body("Invalid or expired token");
        }

        String userId = jwtUtil.extractUserId(token);
        Optional<User> userOptional = userService.getUserById(userId);

        if (userOptional.isEmpty()) {
            System.out.println("user");
            return ResponseEntity.status(404).body("User not found");
        }

        User user = userOptional.get();
        System.out.println("Received Animal IDs: " + salePostRequest.getAnimals());

        // Retrieve animals by IDs from the database
        List<Animal> animals = animalService.getAnimalsByIds(salePostRequest.getAnimals());

        if (animals.isEmpty()) {
            System.out.println("animals");
            return ResponseEntity.status(404).body("No valid animals found for given IDs");
        }

        // Create SalePost with user and animals
        SalePost salePost = new SalePost(
                salePostRequest.getTitle(),
                salePostRequest.getDescription(),
                salePostRequest.getPrice(),
                animals,
                animals.size(), // Number of animals is derived from the list size
                user, // Owner is the authenticated user
                new ArrayList<>() // Empty image list initially
        );

        SalePost savedPost = salePostService.createSalePost(salePost);
        return ResponseEntity.ok(savedPost);
    }
    // ✅ New API: Get sale posts from other users
    @GetMapping("/others")
    public ResponseEntity<List<SalePost>> getSalePostsFromOtherUsers(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", ""); // Remove "Bearer " prefix

        // Validate token
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body(null); // Unauthorized
        }

        // Extract user ID from token
        String loggedInUserId = jwtUtil.extractUserId(token);

        // Fetch all sale posts and filter out those created by the logged-in user
        List<SalePost> allPosts = salePostService.getAllSalePosts();
        List<SalePost> filteredPosts = allPosts.stream()
                .filter(post -> !post.getOwner().getId().equals(loggedInUserId))
                .toList();

        return ResponseEntity.ok(filteredPosts);
    }


    // Upload images to a sale post
    @PostMapping("/{id}/upload-images")
    public ResponseEntity<SalePost> uploadImages(@PathVariable String id, @RequestParam("images") List<MultipartFile> images) {
        Optional<SalePost> salePostOptional = salePostService.getSalePostById(id);

        if (salePostOptional.isPresent()) {
            SalePost salePost = salePostOptional.get();
            List<byte[]> imageDataList = new ArrayList<>();

            try {
                for (MultipartFile image : images) {
                    imageDataList.add(image.getBytes());
                }
            } catch (IOException e) {
                return ResponseEntity.status(500).body(null);
            }

            salePost.setImages(imageDataList);
            SalePost updatedPost = salePostService.updateSalePost(id, salePost);
            return ResponseEntity.ok(updatedPost);
        }

        return ResponseEntity.notFound().build();
    }

    // Update a sale post
    @PutMapping("/{id}")
    public ResponseEntity<SalePost> updateSalePost(@PathVariable String id, @RequestBody SalePost salePost) {
        try {
            SalePost updatedPost = salePostService.updateSalePost(id, salePost);
            return ResponseEntity.ok(updatedPost);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    // Delete a sale post
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSalePost(@PathVariable String id) {
        salePostService.deleteSalePost(id);
        return ResponseEntity.noContent().build();
    }

    // Get sale posts by owner
    @GetMapping("/owner/{ownerId}")
    public List<SalePost> getSalePostsByOwner(@PathVariable String ownerId) {
        return salePostService.getSalePostsByOwner(ownerId);
    }

    // Get sale post with owner details
    @GetMapping("/{id}/details")
    public ResponseEntity<?> getSalePostWithOwnerDetails(@PathVariable String id) {
        Optional<SalePost> salePostOptional = salePostService.getSalePostById(id);

        if (salePostOptional.isPresent()) {
            SalePost salePost = salePostOptional.get();
            User owner = salePost.getOwner();

            return ResponseEntity.ok(
                    new SalePostDetails(
                            salePost.getTitle(),
                            salePost.getDescription(),
                            salePost.getPrice(),
                            owner.getName(),
                            owner.getPhoneNumber(),
                            owner.getAddress(),
                            salePost.getImages()
                    )
            );
        }
        return ResponseEntity.notFound().build();
    }

    private static class SalePostDetails {
        public String title;
        public String description;
        public double price;
        public String ownerName;
        public String phone;
        public String location;
        public List<byte[]> images;

        public SalePostDetails(String title, String description, double price, String ownerName, String phone, String location, List<byte[]> images) {
            this.title = title;
            this.description = description;
            this.price = price;
            this.ownerName = ownerName;
            this.phone = phone;
            this.location = location;
            this.images = images;
        }
    }
}
