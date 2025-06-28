package com.example.farmerapp.services;

import com.example.farmerapp.models.Animal;
import com.example.farmerapp.models.AnimalEvent;
import com.example.farmerapp.repositories.AnimalEventRepository;
import com.example.farmerapp.repositories.AnimalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnimalEventService {

    @Autowired
    private AnimalEventRepository eventRepository;
    @Autowired
    private AnimalRepository animalRepository;

    public AnimalEvent save(AnimalEvent event) {
        return eventRepository.save(event);
    }
    public List<AnimalEvent> getEventsByAnimalId(String animalId) {
        return eventRepository.findByAnimalId(animalId);
    }
    public List<AnimalEvent> getAllEvents() {
        return eventRepository.findAll();
    }
    public void addEventToAnimal(String animalId, AnimalEvent event) {
        Optional<Animal> animalOpt = animalRepository.findById(animalId);
        if (animalOpt.isPresent()) {
            event.setAnimalId(animalId);
            eventRepository.save(event);
        } else {
            throw new IllegalArgumentException("Animal not found.");
        }
    }
    public void deleteEventsByIds(List<String> ids) {
        eventRepository.deleteAllById(ids);
    }
    public List<AnimalEvent> getEventsByType(String eventType) {
        return eventRepository.findAll().stream()
                .filter(event -> eventType.equalsIgnoreCase(event.getEventType()))
                .toList();
    }
    public List<AnimalEvent> getEventsByAnimalIdAndTypeAndDate(String animalId, String eventType, java.time.LocalDate start, java.time.LocalDate end) {
        return getEventsByAnimalId(animalId).stream()
                .filter(event -> eventType.equalsIgnoreCase(event.getEventType())
                        && event.getEventDate() != null
                        && !event.getEventDate().isBefore(start)
                        && !event.getEventDate().isAfter(end))
                .toList();
    }
}
