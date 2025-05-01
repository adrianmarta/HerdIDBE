package com.example.farmerapp.services;

import com.example.farmerapp.models.AnimalEvent;
import com.example.farmerapp.repositories.AnimalEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnimalEventService {

    @Autowired
    private AnimalEventRepository eventRepository;

    public AnimalEvent save(AnimalEvent event) {
        return eventRepository.save(event);
    }
    public List<AnimalEvent> getEventsByAnimalId(String animalId) {
        return eventRepository.findByAnimalId(animalId);
    }
    public List<AnimalEvent> getAllEvents() {
        return eventRepository.findAll();
    }

}
