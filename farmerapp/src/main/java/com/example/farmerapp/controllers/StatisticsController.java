package com.example.farmerapp.controllers;

import com.example.farmerapp.models.Animal;
import com.example.farmerapp.models.AnimalEvent;
import com.example.farmerapp.models.Folder;
import com.example.farmerapp.services.AnimalEventService;
import com.example.farmerapp.services.AnimalService;
import com.example.farmerapp.services.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {
    @Autowired
    private AnimalService animalService;

    @Autowired
    private AnimalEventService eventService;

    @Autowired
    private FolderService folderService;

    @GetMapping
    public ResponseEntity<?> getStatistics(@RequestParam(required = false) String folderId) {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Animal> animals;

        if (folderId != null) {
            Optional<Folder> folderOpt = folderService.getFolderById(folderId);
            if (folderOpt.isEmpty()) {
                return ResponseEntity.status(404).body("Folder not found");
            }
            Folder folder = folderOpt.get();
            if (!folder.getOwner().getId().equals(userId)) {
                return ResponseEntity.status(403).body("You are not authorized to access this folder");
            }
            animals = folder.getAnimals();
        } else {
            animals = animalService.getAnimalByUserId(userId);
        }

        List<Animal> milkProducers = animals.stream()
                .filter(Animal::isProducesMilk)
                .collect(Collectors.toList());

        Map<String, Set<String>> animalsByDisease = new HashMap<>();
        Map<String, Set<String>> animalsByVaccine = new HashMap<>();
        Map<Integer, Integer> birthsByYearInt = new HashMap<>();
        Map<String, Integer> animalBirthYears = new HashMap<>();
        Map<String, String> animalGenders = new HashMap<>();
        Map<Integer, Set<String>> animalsGaveBirthByYear = new HashMap<>();

        for (Animal animal : animals) {
            try {
                if (animal.getBirthDate() != null) {
                    LocalDate birthDate = LocalDate.parse(animal.getBirthDate());
                    animalBirthYears.put(animal.getId(), birthDate.getYear());
                }
            } catch (Exception e) {
                // silently ignore invalid dates
            }

            if (animal.getGender() != null) {
                animalGenders.put(animal.getId(), animal.getGender().toLowerCase()); // normalize to lowercase
            }

            List<AnimalEvent> events = eventService.getEventsByAnimalId(animal.getId());
            for (AnimalEvent event : events) {
                String type = event.getEventType();
                Map<String, Object> details = event.getDetails();

                if ("sickness".equals(type)) {
                    String diagnosis = (String) details.getOrDefault("diagnosis", "Unknown");
                    animalsByDisease.computeIfAbsent(diagnosis, d -> new HashSet<>()).add(animal.getId());
                }

                if ("vaccination".equals(type)) {
                    String vaccine = (String) details.getOrDefault("vaccineName", "Unknown");
                    animalsByVaccine.computeIfAbsent(vaccine, v -> new HashSet<>()).add(animal.getId());
                }

                if ("birth".equals(type)) {
                    LocalDate date = event.getEventDate();
                    if (date != null) {
                        int year = date.getYear();
                        birthsByYearInt.merge(year, 1, Integer::sum);
                        animalsGaveBirthByYear.computeIfAbsent(year, y -> new HashSet<>()).add(animal.getId());
                    }
                }
            }
        }

        Map<String, Integer> diseaseCounts = animalsByDisease.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size()));

        Map<String, Integer> vaccineCounts = animalsByVaccine.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size()));

        Map<String, Integer> birthsByYear = birthsByYearInt.entrySet().stream()
                .collect(Collectors.toMap(e -> String.valueOf(e.getKey()), Map.Entry::getValue));

        Map<String, Set<String>> femalesGaveBirthByYear = animalsGaveBirthByYear.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> String.valueOf(e.getKey()),
                        e -> e.getValue().stream()
                                .filter(id -> "f".equalsIgnoreCase(animalGenders.get(id)))
                                .collect(Collectors.toSet())
                ));

        Map<String, Integer> eligibleFemalesByYear = new HashMap<>();
        for (Integer year : birthsByYearInt.keySet()) {
            int eligibleCount = (int) animalBirthYears.entrySet().stream()
                    .filter(entry -> entry.getValue() <= year - 1)
                    .filter(entry -> "f".equals(animalGenders.get(entry.getKey())))
                    .count();
            eligibleFemalesByYear.put(String.valueOf(year), eligibleCount);
        }


        Map<String, Object> response = new HashMap<>();
        response.put("totalAnimalCount", animals.size());
        response.put("milkProducerCount", milkProducers.size());
        response.put("birthsByYear", birthsByYear);
        response.put("diseaseCounts", diseaseCounts);
        response.put("vaccineCounts", vaccineCounts);
        response.put("eligibleFemalesByYear", eligibleFemalesByYear);
        response.put("femalesGaveBirthByYear", femalesGaveBirthByYear);

        return ResponseEntity.ok(response);
    }
}
