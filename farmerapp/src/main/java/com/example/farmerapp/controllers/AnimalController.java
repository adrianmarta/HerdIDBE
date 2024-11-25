package com.example.farmerapp.controllers;

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

@RestController
@RequestMapping("/api/animals")
public class AnimalController {

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
}
