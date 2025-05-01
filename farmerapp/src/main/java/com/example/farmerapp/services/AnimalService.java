package com.example.farmerapp.services;



import com.example.farmerapp.models.Animal;
import com.example.farmerapp.models.AnimalEvent;
import com.example.farmerapp.models.User;
import com.example.farmerapp.repositories.AnimalEventRepository;
import com.example.farmerapp.repositories.AnimalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AnimalService {

    @Autowired
    private AnimalRepository animalRepository;
    @Autowired
    private AnimalEventRepository animalEventRepository;

    // Create a new animal
    public Animal createAnimal(Animal animal) {
        return animalRepository.save(animal);
    }

    // Get all animals
    public List<Animal> getAllAnimals() {
        return animalRepository.findAll();
    }

    // Get animal by ID
    public Optional<Animal> getAnimalById(String id) {
        return animalRepository.findById(id);
    }

    // Update an animal
    public Animal updateAnimal(String id, Animal animal) {
        if (animalRepository.existsById(id)) {
            animal.setId(id);  // Ensure the ID remains the same during update
            return animalRepository.save(animal);
        } else {
            throw new IllegalArgumentException("Animal not found for update.");
        }
    }
    public boolean isAnimalExist(String animalId) {
        return animalRepository.existsById(animalId);
    }

    // Delete an animal
    public void deleteAnimal(String id) {
        animalRepository.deleteById(id);
    }
    public List<Animal> getAnimalByUserId(String id)
    {
       return animalRepository.findByOwnerId(id);
    }
    public List<Animal> getAnimalsByIds(List<String> animalIds) {
        System.out.println("Fetching animals for IDs: " + animalIds);

        if (animalIds == null || animalIds.isEmpty()) {
            System.out.println("❌ Error: animalIds list is NULL or empty");
            return List.of();
        }

        List<Animal> animals = animalRepository.findAnimalsByIds(animalIds);
        System.out.println("✔ Found Animals: " + animals.size());

        return animals;
    }
    public void addEventToAnimal(String animalId, AnimalEvent event) {
        Optional<Animal> animalOpt = animalRepository.findById(animalId);
        if (animalOpt.isPresent()) {
            event.setAnimalId(animalId);
            animalEventRepository.save(event);
        } else {
            throw new IllegalArgumentException("Animal not found.");
        }
    }

    public void addPhotoToAnimal(String animalId, byte[] photo) {
        Optional<Animal> animalOpt = animalRepository.findById(animalId);
        if (animalOpt.isPresent()) {
            Animal animal = animalOpt.get();
            animal.getPhotos().add(photo);
            animalRepository.save(animal);
        } else {
            throw new IllegalArgumentException("Animal not found.");
        }
    }
}

