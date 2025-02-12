package com.example.farmerapp.controllers;

import com.example.farmerapp.FolderRequest;
import com.example.farmerapp.JwtUtil;
import com.example.farmerapp.models.Folder;
import com.example.farmerapp.models.Animal;
import com.example.farmerapp.models.User;
import com.example.farmerapp.repositories.UserRepository;
import com.example.farmerapp.services.FolderService;
import com.example.farmerapp.services.AnimalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/folders")
public class FolderController {

    @Autowired
    private FolderService folderService;
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AnimalService animalService;

    @Autowired
    private UserRepository userRepository;

    // Get all folders for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Folder>> getFoldersByUserId(@PathVariable String userId) {
        List<Folder> folders = folderService.getFoldersByUserId(userId);
        if (folders.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }
        return ResponseEntity.ok(folders);
    }
    @GetMapping("/user")
    public ResponseEntity<List<Folder>> getFoldersByToken(
            @RequestHeader("Authorization") String authHeader) {

        // Extract token and remove "Bearer " prefix
        String token = authHeader.replace("Bearer ", "");

        // Validate token
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body(null); // Unauthorized
        }

        // Extract user ID from token
        String userId = jwtUtil.extractUserId(token);

        // Fetch herds (folders) for this user
        List<Folder> folders = folderService.getFoldersByUserId(userId);
        if (folders.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }

        return ResponseEntity.ok(folders);
    }
    // Remove animals from a folder
    @PutMapping("/{folderId}/remove-animals")
    public ResponseEntity<Folder> removeAnimalsFromFolder(@PathVariable String folderId, @RequestBody List<String> animalIds) {
        Optional<Folder> folderOptional = folderService.getFolderById(folderId);
        if (folderOptional.isPresent()) {
            Folder folder = folderOptional.get();
            // Remove animals with specified IDs from the folder's animal list
            List<Animal> updatedAnimals = folder.getAnimals().stream()
                    .filter(animal -> !animalIds.contains(animal.getId()))
                    .collect(Collectors.toList());
            folder.setAnimals(updatedAnimals);
            folderService.updateFolder(folderId, folder);
            return ResponseEntity.ok(folder);
        }
        return ResponseEntity.status(404).body(null);
    }

    // Create a new folder
    @PostMapping
    public ResponseEntity<Folder> createFolder(
            @RequestBody FolderRequest folderRequest,
            @RequestHeader("Authorization") String authHeader) {

        // Extract token from the Authorization header (remove "Bearer " prefix)
        String token = authHeader.replace("Bearer ", "");

        // Validate the token
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body(null); // Unauthorized
        }

        // Extract user ID from the token
        String userId = jwtUtil.extractUserId(token);

        // Find the user based on the extracted ID
        Optional<User> ownerOptional = userRepository.findById(userId);
        if (ownerOptional.isEmpty()) {
            return ResponseEntity.status(404).body(null); // Owner not found
        }

        // Create and save the new folder
        User owner = ownerOptional.get();
        Folder folder = new Folder(folderRequest.getName(), owner);
        Folder savedFolder = folderService.createFolder(folder);

        return ResponseEntity.ok(savedFolder);
    }

    @GetMapping("/{folderId}/animals")
    public ResponseEntity<?> getAnimalsInFolder(@PathVariable String folderId) {
        Optional<Folder> folderOptional = folderService.getFolderById(folderId);

        if (folderOptional.isPresent()) {
            Folder folder = folderOptional.get();
            List<Animal> animals = folder.getAnimals(); // Get animals from the folder
            return ResponseEntity.ok(animals);
        } else {
            return ResponseEntity.status(404).body("Folder not found");
        }
    }
    @PutMapping("/{folderId}/add-existing-animal/{animalId}")
    public ResponseEntity<?> addExistingAnimalToFolder(@PathVariable String folderId, @PathVariable String animalId,
                                                       @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(403).body("Missing or invalid authorization header.");
        }
        // Log the token (be cautious about logging sensitive information in production)
        System.out.println("Token: " + authorizationHeader);
        Optional<Folder> folderOptional = folderService.getFolderById(folderId);
        Optional<Animal> animalOptional = animalService.getAnimalById(animalId);

        if (folderOptional.isPresent() && animalOptional.isPresent()) {
            Folder folder = folderOptional.get();
            Animal animal = animalOptional.get();

            // Check if the animal is already in the folder
            if (folder.getAnimals().contains(animal)) {
                return ResponseEntity.status(400).body("Animal already exists in the folder.");
            }

            // Add the animal to the folder
            folder.getAnimals().add(animal);
            folderService.updateFolder(folderId, folder); // Save the updated folder

            return ResponseEntity.ok(folder);
        }

        return ResponseEntity.status(404).body("Folder or Animal not found.");
    }


    // Add animals to a folder
    @PutMapping("/{folderId}/add-animals")
    public ResponseEntity<Folder> addAnimalsToFolder(@PathVariable String folderId, @RequestBody List<String> animalIds) {
        Optional<Folder> folderOptional = folderService.getFolderById(folderId);
        if (folderOptional.isEmpty()) {
            return ResponseEntity.status(404).body(null); // Folder not found
        }

        Folder folder = folderOptional.get();
        List<Animal> existingAnimals = folder.getAnimals(); // Get already added animals

        List<Animal> animalsToAdd = animalService.getAnimalsByIds(animalIds) // Fetch animals by IDs
                .stream()
                .filter(animal -> !existingAnimals.contains(animal)) // Only add if not already in the folder
                .toList();

        if (animalsToAdd.isEmpty()) {
            return ResponseEntity.status(400).body(null); // No new animals to add
        }

        folder.getAnimals().addAll(animalsToAdd);
        folderService.updateFolder(folderId, folder);
        return ResponseEntity.ok(folder);
    }

    // Update a folder
    @PutMapping("/{id}")
    public ResponseEntity<Folder> updateFolder(@PathVariable String id, @RequestBody Folder folder) {
        return ResponseEntity.ok(folderService.updateFolder(id, folder));
    }
    @GetMapping("/compare/{folderId1}/{folderId2}")
    public ResponseEntity<List<String>> compareFolders(
            @PathVariable String folderId1,
            @PathVariable String folderId2) {
        try {
            // Fetch the animals from the service and map to their IDs
            List<String> animalIds = folderService.compareFolders(folderId1, folderId2)
                    .stream()
                    .map(Animal::getId) // Extract only the IDs
                    .collect(Collectors.toList());
            return ResponseEntity.ok(animalIds);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(null); // Folders not found
        }
    }


    // Delete a folder
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFolder(@PathVariable String id) {
        folderService.deleteFolder(id);
        return ResponseEntity.noContent().build();
    }


}
