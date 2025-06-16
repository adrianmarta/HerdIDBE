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
}
