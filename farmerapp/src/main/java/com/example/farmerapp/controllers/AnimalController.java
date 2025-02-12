package com.example.farmerapp.controllers;

import com.example.farmerapp.JwtUtil;
import com.example.farmerapp.models.Animal;
import com.example.farmerapp.models.User;
import com.example.farmerapp.repositories.AnimalRepository;
import com.example.farmerapp.repositories.UserRepository;
import com.example.farmerapp.services.AnimalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/animals")
public class AnimalController {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AnimalService animalService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AnimalRepository animalRepository;

    // Get all animals
    @GetMapping
    public List<Animal> getAllAnimals() {
        return animalService.getAllAnimals();
    }

    // Get animal by ID
    @PostMapping
    public ResponseEntity<Animal> createAnimal(@RequestBody Animal animal) {
        Optional<User> owner = userRepository.findById(animal.getOwner().getId());
        if (owner.isPresent()) {
            animal.setOwner(owner.get());
            Animal savedAnimal = animalService.createAnimal(animal);
            User user= owner.get();
            user.getAnimals().add(animal);
            userRepository.save(user);

            return ResponseEntity.ok(savedAnimal);
        }
        return ResponseEntity.status(404).body(null);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Animal> getAnimalById(@PathVariable String id) {
        Optional<Animal> animal = animalRepository.findById(id);
        return animal.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping("/list")
    public ResponseEntity<List<Animal>> getAnimalsByIds(@RequestParam List<String> ids) {
        List<Animal> animals = animalRepository.findAllById(ids);
        if (animals.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }
        return ResponseEntity.ok(animals);
    }


    // Update an animal by ID
    @PutMapping("/{id}")
    public ResponseEntity<Animal> updateAnimal(@PathVariable String id, @RequestBody Animal animal) {
        try {
            Animal updatedAnimal = animalService.updateAnimal(id, animal);
            return ResponseEntity.ok(updatedAnimal);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    // Delete an animal by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnimal(@PathVariable String id) {
        animalService.deleteAnimal(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/owner")
    public ResponseEntity<?> getAnimalsByToken(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", ""); // Remove "Bearer " prefix

        if (jwtUtil.validateToken(token)) {
            String ownerId = jwtUtil.extractUserId(token); // Extract user ID from token
            List<Animal> animals = animalService.getAnimalByUserId(ownerId);

            if (animals.isEmpty()) {
                return ResponseEntity.status(404).body("No animals found for this owner");
            }
            return ResponseEntity.ok(animals);
        } else {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }
    }
    // Get animals by owner ID
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Animal>> getAnimalsByOwnerId(@PathVariable String ownerId) {
        List<Animal> animals = animalService.getAnimalByUserId(ownerId);
        if (animals.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }
        return ResponseEntity.ok(animals);
    }
    @GetMapping("/exists/{animalId}")
    public ResponseEntity<Boolean> checkAnimalExists(@PathVariable String animalId) {
        boolean exists = animalService.isAnimalExist(animalId);
        return ResponseEntity.ok(exists);
    }
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteAnimalsByIds(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String animalIds) { // Accept as String and split manually

        String token = authHeader.replace("Bearer ", ""); // Remove "Bearer " prefix

        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }

        String ownerId = jwtUtil.extractUserId(token); // Extract user ID from token
        List<String> animalIdList = List.of(animalIds.split(",")); // Convert to list

        System.out.println("Animal IDs received for deletion: " + animalIdList); // Debugging log

        List<Animal> animals = animalService.getAnimalsByIds(animalIdList);
        System.out.println("Animals found: " + animals.size()); // Debugging log

        if (animals.isEmpty()) {
            return ResponseEntity.status(404).body("No animals found with the given IDs");
        }

        // Check if all animals belong to the authenticated owner
        List<Animal> animalsToDelete = animals.stream()
                .filter(animal -> animal.getOwner().getId().equals(ownerId))
                .toList();

        if (animalsToDelete.isEmpty()) {
            return ResponseEntity.status(403).body("You are not authorized to delete these animals");
        }

        animalRepository.deleteAll(animalsToDelete);
        return ResponseEntity.ok("Successfully deleted " + animalsToDelete.size() + " animals");
    }



}
