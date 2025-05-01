package com.example.farmerapp.controllers;

import com.example.farmerapp.dto.*;
import com.example.farmerapp.AnimalEventMapper;
import com.example.farmerapp.models.AnimalEvent;
import com.example.farmerapp.services.AnimalEventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
public class AnimalEventController {

    @Autowired
    private AnimalEventService eventService;
    private final ObjectMapper objectMapper;

    public AnimalEventController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostMapping
    public ResponseEntity<AnimalEvent> createEvent(
            @RequestParam String animalId,
            @RequestBody Map<String, Object> payload) {

        String eventType = (String) payload.get("eventType");

        BaseAnimalEventDTO dto = switch (eventType) {
            case "vaccination" -> new VaccinationEventDTO();
            case "sickness" -> new SicknessEventDTO();
            case "birth" -> new BirthEventDTO();
            default -> throw new IllegalArgumentException("Invalid event type");
        };

        dto = (BaseAnimalEventDTO) objectMapper.convertValue(payload, dto.getClass()); // Use injected mapper

        AnimalEvent event = AnimalEventMapper.fromDto(animalId, dto);
        AnimalEvent saved = eventService.save(event);
        return ResponseEntity.ok(saved);
    }
    @GetMapping("/animal/{animalId}")
    public ResponseEntity<List<AnimalEvent>> getEventsByAnimalId(@PathVariable String animalId) {
        List<AnimalEvent> events = eventService.getEventsByAnimalId(animalId);
        return ResponseEntity.ok(events);
    }
    @GetMapping
    public ResponseEntity<List<AnimalEvent>> getAllEvents() {
        List<AnimalEvent> events = eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }

}
