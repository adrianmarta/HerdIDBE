package com.example.farmerapp.controllers;

import com.example.farmerapp.dto.*;
import com.example.farmerapp.models.AnimalEventMapper;
import com.example.farmerapp.models.Animal;
import com.example.farmerapp.models.AnimalEvent;
import com.example.farmerapp.services.AnimalEventService;
import com.example.farmerapp.services.AnimalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.Objects;

@RestController
@RequestMapping("/api/events")
public class AnimalEventController {

    @Autowired
    private AnimalEventService eventService;

    @Autowired
    private AnimalService animalService;

    private final ObjectMapper objectMapper;

    public AnimalEventController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @PostMapping("/{id}/add-event")// adauga eveniment la un animal
    public ResponseEntity<String> addEventToAnimal(@PathVariable String id, @RequestBody AnimalEvent event) {
        try {
            eventService.addEventToAnimal(id, event);
            return ResponseEntity.ok("Eveniment adăugat cu succes.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body("Animalul nu a fost găsit.");
        }
    }
    @GetMapping("/animal/{animalId}")//evenimentele unui animal
    public ResponseEntity<List<AnimalEvent>> getEventsByAnimalId(@PathVariable String animalId) {
        List<AnimalEvent> events = eventService.getEventsByAnimalId(animalId);
        return ResponseEntity.ok(events);
    }

    @PostMapping("/animal/{animalId}")
    public ResponseEntity<AnimalEvent> createEventForAnimal(
            @PathVariable String animalId,
            @RequestBody Map<String, Object> payload) {
        String eventType = (String) payload.get("eventType");
        BaseAnimalEventDTO dto = switch (eventType) {
            case "vaccination" -> new VaccinationEventDTO();
            case "sickness" -> new SicknessEventDTO();
            case "birth" -> new BirthEventDTO();
            case "death" -> new DeathEventDTO();
            default -> throw new IllegalArgumentException("Invalid event type: " + eventType);
        };
        dto = objectMapper.convertValue(payload, dto.getClass());
        AnimalEvent event = AnimalEventMapper.fromDto(animalId, dto);
        AnimalEvent saved = eventService.save(event);
        if ("death".equals(eventType)) {
            animalService.deleteAnimal(animalId);
        }
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<AnimalEvent>> getAllEvents() {
        List<AnimalEvent> events = eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/types")
    public ResponseEntity<Map<String, Map<String, String>>> getEventTypes() {
        Map<String, Map<String, String>> eventTypes = new HashMap<>();
        
        // Vaccination event details
        Map<String, String> vaccinationDetails = new HashMap<>();
        vaccinationDetails.put("description", "Record a vaccination given to an animal");
        vaccinationDetails.put("requiredFields", "vaccineName, dosage");
        eventTypes.put("vaccination", vaccinationDetails);

        // Sickness event details
        Map<String, String> sicknessDetails = new HashMap<>();
        sicknessDetails.put("description", "Record a sickness or medical treatment");
        sicknessDetails.put("requiredFields", "diagnosis, treatment");
        eventTypes.put("sickness", sicknessDetails);

        // Birth event details
        Map<String, String> birthDetails = new HashMap<>();
        birthDetails.put("description", "Record the birth of a new animal");
        birthDetails.put("requiredFields", "calfGender, notes");
        eventTypes.put("birth", birthDetails);

        // Death event details
        Map<String, String> deathDetails = new HashMap<>();
        deathDetails.put("description", "Record the death of an animal (will remove animal from active list)");
        deathDetails.put("requiredFields", "causeOfDeath, notes");
        eventTypes.put("death", deathDetails);

        return ResponseEntity.ok(eventTypes);
    }

    @GetMapping("/sickness-names")//returneaza numele bolilor inregistrate de un utilizator
    public ResponseEntity<List<String>> getSicknessNames() {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Animal> userAnimals = animalService.getAnimalByUserId(userId);
        
        List<String> sicknessNames = userAnimals.stream()
            .flatMap(animal -> eventService.getEventsByAnimalId(animal.getId()).stream())
            .filter(event -> "sickness".equals(event.getEventType()))
            .map(event -> (String) event.getDetails().get("diagnosis"))
            .filter(diagnosis -> diagnosis != null && !diagnosis.trim().isEmpty())
            .distinct()
            .sorted()
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(sicknessNames);
    }

    @GetMapping("/vaccine-names")//returneaza numele vaccinurilor inregistrate de un utilizator
    public ResponseEntity<List<String>> getVaccineNames() {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Animal> userAnimals = animalService.getAnimalByUserId(userId);
        
        List<String> vaccineNames = userAnimals.stream()
            .flatMap(animal -> eventService.getEventsByAnimalId(animal.getId()).stream())
            .filter(event -> "vaccination".equals(event.getEventType()))
            .map(event -> (String) event.getDetails().get("vaccineName"))
            .filter(vaccineName -> vaccineName != null && !vaccineName.trim().isEmpty())
            .distinct()
            .sorted()
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(vaccineNames);
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteEventsBulk(@RequestBody List<String> ids) {
        eventService.deleteEventsByIds(ids);
        return ResponseEntity.ok("Evenimentele au fost șterse cu succes.");
    }

    @GetMapping("/by-type/{eventType}")// returneaza animalele care au evenimentele inregistrata intr-o perioada
    public ResponseEntity<List<Animal>> getAnimalsByEventTypeAndDate(
            @PathVariable String eventType,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Animal> userAnimals = animalService.getAnimalByUserId(userId);
        java.time.LocalDate start = java.time.LocalDate.parse(startDate);
        java.time.LocalDate end = java.time.LocalDate.parse(endDate);
        List<Animal> filtered = userAnimals.stream()
                .filter(animal -> !eventService.getEventsByAnimalIdAndTypeAndDate(animal.getId(), eventType, start, end).isEmpty())
                .toList();
        return ResponseEntity.ok(filtered);
    }

}
