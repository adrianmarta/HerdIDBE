package com.example.farmerapp.controllers;

import com.example.farmerapp.JwtUtil;
import com.example.farmerapp.models.*;
import com.example.farmerapp.services.AnimalService;
import com.example.farmerapp.services.FavoriteService;
import com.example.farmerapp.services.SalePostService;
import com.example.farmerapp.services.UserService;
import com.example.farmerapp.services.InterestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
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
    @Autowired
    private FavoriteService favoriteService;
    @Autowired
    private InterestService interestService;

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
    // Get sale posts from other users (not owned by the current user)
    @GetMapping("/others")
    public ResponseEntity<List<SalePost>> getSalePostsFromOtherUsers(
            ) {

        String ownerId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<SalePost> allPosts = salePostService.getAllSalePosts();
        List<SalePost> filteredPosts = allPosts.stream()
                .filter(post -> !post.getOwner().getId().equals(ownerId))
                .toList();

        return ResponseEntity.ok(filteredPosts);
    }
    // Create a sale post
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createSalePost(

            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("price") double price,
            @RequestParam(value = "animals", required = false) List<String> animals,
            @RequestParam(value = "images", required = false) List<MultipartFile> images) {

        String ownerId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> userOptional = userService.getUserById(ownerId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        User user = userOptional.get();
        List<Animal> animalList = animalService.getAnimalsByIds(animals != null ? animals : List.of());

        List<byte[]> imageDataList = new ArrayList<>();
        if (images != null) {
            try {
                for (MultipartFile image : images) {
                    imageDataList.add(image.getBytes());
                }
            } catch (IOException e) {
                return ResponseEntity.status(500).body("Error processing images");
            }
        }

        SalePost salePost = new SalePost(title, description, price, animalList, animalList.size(), user, imageDataList, LocalDateTime.now());
        SalePost savedPost = salePostService.createSalePost(salePost);
        return ResponseEntity.ok(savedPost);
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

            // Extract unique species from animals, handling null animals
            List<String> species = salePost.getAnimals().stream()
                .filter(animal -> animal != null) // Filter out null animals
                .map(Animal::getSpecies)// Filter out null species
                .distinct()
                .toList();

            return ResponseEntity.ok(
                    new SalePostDetails(
                            salePost.getTitle(),
                            salePost.getDescription(),
                            salePost.getPrice(),
                            salePost.getNumberOfAnimals(),
                            owner.getName(),
                            owner.getPhoneNumber(),
                            owner.getAddress(),
                            salePost.getImages(),
                            salePost.getCreationDate(),
                            species
                    )
            );
        }
        return ResponseEntity.notFound().build();
    }

    // Add favorite
    @PostMapping("/{id}/favorite")
    public ResponseEntity<?> addFavorite(

            @PathVariable String id) {
        String ownerId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> userOpt = userService.getUserById(ownerId);
        Optional<SalePost> postOpt = salePostService.getSalePostById(id);
        if (userOpt.isEmpty() || postOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User or SalePost not found");
        }
        favoriteService.addFavorite(userOpt.get(), postOpt.get());
        return ResponseEntity.ok().build();
    }

    // Remove favorite
    @DeleteMapping("/{id}/favorite")
    public ResponseEntity<?> removeFavorite(

            @PathVariable String id) {
        String ownerId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        favoriteService.removeFavorite(ownerId, id);
        return ResponseEntity.ok().build();
    }

    // Get current user's favorites
    @GetMapping("/user/favorites")
    public ResponseEntity<?> getUserFavorites() {
        String ownerId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseEntity.ok(favoriteService.getFavoritesByUser(ownerId));
    }

    // Mark interest
    @PostMapping("/{id}/interest")
    public ResponseEntity<?> addInterest(

            @PathVariable String id) {
        String ownerId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> userOpt = userService.getUserById(ownerId);
        Optional<SalePost> postOpt = salePostService.getSalePostById(id);
        if (userOpt.isEmpty() || postOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User or SalePost not found");
        }
        interestService.addInterest(userOpt.get(), postOpt.get());
        return ResponseEntity.ok().build();
    }

    // Get interested users for a post (for owner)
    @GetMapping("/{id}/interests")
    public ResponseEntity<?> getInterestsForPost(

            @PathVariable String id) {
        // Optionally, check if the current user is the owner of the post
        return ResponseEntity.ok(interestService.getInterestsBySalePost(id));
    }

    // Get all posts the current user is interested in
    @GetMapping("/interested")
    public ResponseEntity<?> getUserInterests() {
        String ownerId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseEntity.ok(interestService.getInterestsByUser(ownerId));
    }

    // Get filtered sale posts
    @PostMapping("/filter")
    public ResponseEntity<List<SalePost>> getFilteredSalePosts(

            @RequestBody SalePostFilterDTO filter) {
        String ownerId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        filter.setUserId(ownerId); // Set the user ID to exclude their own posts

        List<SalePost> filteredPosts = salePostService.getFilteredSalePosts(filter);
        return ResponseEntity.ok(filteredPosts);
    }

    @GetMapping("/user/posts")
    public ResponseEntity<?> getCurrentUserPosts() {
        String ownerId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<SalePost> userPosts = salePostService.getSalePostsByOwner(ownerId);
        return ResponseEntity.ok(userPosts);
    }

    private static class SalePostDetails {
        public String title;
        public String description;
        public double price;
        public int numberOfAnimals;
        public String ownerName;
        public String phone;
        public String location;
        public List<byte[]> images;
        public java.time.LocalDateTime creationDate;
        public List<String> species;

        public SalePostDetails(String title, String description, double price, int numberOfAnimals, 
                             String ownerName, String phone, String location, 
                             List<byte[]> images, java.time.LocalDateTime creationDate,
                             List<String> species) {
            this.title = title;
            this.description = description;
            this.price = price;
            this.numberOfAnimals = numberOfAnimals;
            this.ownerName = ownerName;
            this.phone = phone;
            this.location = location;
            this.images = images;
            this.creationDate = creationDate;
            this.species = species;
        }
    }
}
