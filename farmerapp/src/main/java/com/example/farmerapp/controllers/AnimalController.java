package com.example.farmerapp.controllers;

import com.example.farmerapp.JwtUtil;
import com.example.farmerapp.models.*;
import com.example.farmerapp.repositories.AnimalRepository;
import com.example.farmerapp.repositories.UserRepository;
import com.example.farmerapp.services.AnimalEventService;
import com.example.farmerapp.services.AnimalService;
import com.example.farmerapp.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.chrono.ChronoLocalDate;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/animals")
public class AnimalController {

    private static final Logger logger = LoggerFactory.getLogger(AnimalController.class);

    @Autowired
    private AnimalService animalService;
    @Autowired
    private UserService userService;
    @Autowired
    private AnimalEventService eventService;


    // Get all animals
    @GetMapping
    public List<Animal> getAllAnimals() {
        return animalService.getAllAnimals();
    }


    @PostMapping
    public ResponseEntity<Animal> createAnimal(@RequestBody Animal animal) {
        String ownerId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<User> owner = userService.getUserById(ownerId);
        if (owner.isPresent()) {
            try {
                animal.setOwner(owner.get());
                Animal savedAnimal = animalService.createAnimal(animal);
                User user = owner.get();
                user.getAnimals().add(animal);
                userService.saveUser(user);
                return ResponseEntity.ok(savedAnimal);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(409).body(null); // 409 Conflict for duplicate resource
            }
        }
        return ResponseEntity.status(404).body(null);
    }
    @GetMapping("/by-event-type")
    public ResponseEntity<List<Animal>> getAnimalsByEventType(
            @RequestParam String eventType,
            @RequestParam String eventValue
            ) {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            List<Animal> userAnimals = animalService.getAnimalByUserId(userId);
            List<Animal> matchingAnimals = userAnimals.stream()
                    .filter(animal -> {
                        List<AnimalEvent> events = animal.getEvents();
                        if (events == null) return false;
                        return events.stream().anyMatch(event ->
                                event.getEventType().equalsIgnoreCase(eventType) &&
                                        event.getDetails().entrySet().stream()
                                                .anyMatch(entry -> entry.getValue().toString().equalsIgnoreCase(eventValue))
                        );
                    })
                    .collect(Collectors.toList());
            if (matchingAnimals.isEmpty()) {
                return ResponseEntity.status(404).body(null);
            }
            return ResponseEntity.ok(matchingAnimals);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> createAnimalsBatch(
            @RequestBody List<Animal> animals
    ) {
        String ownerId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> optionalUser = userService.getUserById(ownerId);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }
        User owner = optionalUser.get();
        int addedCount = 0;
        int skippedCount = 0;
        for (Animal animal : animals) {
            if (animalService.isAnimalExist(animal.getId())) {
                skippedCount++;
                continue;
            }
            animal.setOwner(owner);
            animalService.saveAnimal(animal);
            owner.getAnimals().add(animal);
            addedCount++;
        }
        userService.saveUser(owner);
        Map<String, Object> result = new HashMap<>();
        result.put("added", addedCount);
        result.put("skipped", skippedCount);
        result.put("total", animals.size());
        return ResponseEntity.ok(result);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Animal> getAnimalById(@PathVariable String id) {
        Optional<Animal> animal = animalService.getAnimalById(id);
        return animal.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping("/list")
    public ResponseEntity<List<Animal>> getAnimalsByIds(@RequestParam List<String> ids) {
        List<Animal> animals = animalService.getAnimalsByIds(ids);
        if (animals.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }
        return ResponseEntity.ok(animals);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Animal> updateAnimal(@PathVariable String id, @RequestBody AnimalUpdateDTO updateDTO) {
        return ResponseEntity.ok(animalService.updateAnimal(id, updateDTO));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnimal(@PathVariable String id) {
        animalService.deleteAnimal(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/owner-animals")
    public ResponseEntity<?> getMyAnimals() {
        try {
            String userId = (String) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal();

            List<Animal> animals = animalService.getAnimalByUserId(userId);

            if (animals.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Nu s-au găsit animale pentru utilizatorul autentificat.");
            }

            return ResponseEntity.ok(animals);

        } catch (ClassCastException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token invalid sau utilizator neautentificat.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("A apărut o eroare internă: " + e.getMessage());
        }
    }
    @GetMapping("/exists/{animalId}")
    public ResponseEntity<Boolean> checkAnimalExists(@PathVariable String animalId) {
        boolean exists = animalService.isAnimalExist(animalId);
        return ResponseEntity.ok(exists);
    }
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteAnimalsByIds(
            @RequestParam String animalIds) {
        String ownerId = (String) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        List<String> animalIdList = List.of(animalIds.split(",")); // Convert to list
        List<Animal> animals = animalService.getAnimalsByIds(animalIdList);
        if (animals.isEmpty()) {
            return ResponseEntity.status(404).body("No animals found with the given IDs");
        }
        List<Animal> animalsToDelete = animals.stream()
                .filter(animal -> animal.getOwner().getId().equals(ownerId))
                .toList();
        if (animalsToDelete.isEmpty()) {
            return ResponseEntity.status(403).body("You are not authorized to delete these animals");
        }
        animalService.deleteAllAnimals(animalsToDelete);
        return ResponseEntity.ok("Successfully deleted " + animalsToDelete.size() + " animals");
    }
    @GetMapping("/search")
    public ResponseEntity<List<Animal>> searchAnimals(
            @RequestParam String query) {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Animal> animals = animalService.searchAnimals(query, userId);
        if (animals.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }
        return ResponseEntity.ok(animals);
    }
    @GetMapping("/species")
    public ResponseEntity<List<Map<String, String>>> getAvailableSpecies() {
        List<Map<String, String>> speciesList = Arrays.stream(AnimalSpecies.values())
            .map(species -> {
                Map<String, String> speciesMap = new HashMap<>();
                speciesMap.put("value", species.name());
                speciesMap.put("label", species.getDisplayName());
                return speciesMap;
            })
            .collect(Collectors.toList());
        return ResponseEntity.ok(speciesList);
    }
    @GetMapping("/by-birth-date")
    public ResponseEntity<List<Animal>> getAnimalsByBirthDate(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestHeader("Authorization") String token) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);
        
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Animal> userAnimals = animalService.getAnimalByUserId(userId);
        
        List<Animal> animals = userAnimals.stream()
            .filter(animal -> {
                List<AnimalEvent> events = eventService.getEventsByAnimalId(animal.getId());
                if (events == null || events.isEmpty()) {
                    return false;
                }
                
                return events.stream()
                    .anyMatch(event -> 
                        "birth".equals(event.getEventType()) &&
                        event.getEventDate() != null &&
                        !event.getEventDate().isBefore(start) &&
                        !event.getEventDate().isAfter(end)
                    );
            })
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(animals);
    }
    @GetMapping("/by-sickness")
    public ResponseEntity<List<Animal>> getAnimalsBySickness(
            @RequestParam String sicknessName,
            @RequestHeader("Authorization") String token) {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Animal> userAnimals = animalService.getAnimalByUserId(userId);
        
        List<Animal> animals = userAnimals.stream()
            .filter(animal -> {
                List<AnimalEvent> events = eventService.getEventsByAnimalId(animal.getId());
                if (events == null || events.isEmpty()) {
                    return false;
                }

                return events.stream()
                    .anyMatch(event -> 
                        "sickness".equals(event.getEventType()) &&
                        event.getDetails() != null &&
                        sicknessName.equalsIgnoreCase((String) event.getDetails().get("diagnosis"))
                    );
            })
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(animals);
    }
    @GetMapping("/by-vaccination")
    public ResponseEntity<List<Animal>> getAnimalsByVaccination(
            @RequestParam String vaccineName,
            @RequestHeader("Authorization") String token) {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Animal> userAnimals = animalService.getAnimalByUserId(userId);
        
        List<Animal> animals = userAnimals.stream()
            .filter(animal -> {
                List<AnimalEvent> events = eventService.getEventsByAnimalId(animal.getId());
                if (events == null || events.isEmpty()) {
                    return false;
                }

                return events.stream()
                    .anyMatch(event -> 
                        "vaccination".equals(event.getEventType()) &&
                        event.getDetails() != null &&
                        vaccineName.equalsIgnoreCase((String) event.getDetails().get("vaccineName"))
                    );
            })
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(animals);
    }
}
