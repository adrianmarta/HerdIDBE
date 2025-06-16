package com.example.farmerapp.services;



import com.example.farmerapp.models.Animal;
import com.example.farmerapp.models.AnimalEvent;
import com.example.farmerapp.models.AnimalUpdateDTO;
import com.example.farmerapp.models.User;
import com.example.farmerapp.repositories.AnimalEventRepository;
import com.example.farmerapp.repositories.AnimalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AnimalService {

    @Autowired
    private AnimalRepository animalRepository;

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
public Animal saveAnimal(Animal animal)
{
    return animalRepository.save(animal);
}
    // Update an animal
    public Animal updateAnimal(String id, AnimalUpdateDTO updateDTO) {
        Optional<Animal> OptionalAnimal= animalRepository.findById(id);
        if (OptionalAnimal.isPresent()){
            Animal animal=OptionalAnimal.get();
            animal.setGender(updateDTO.getGender());
            animal.setSpecies(updateDTO.getSpecies());
            animal.setProducesMilk(updateDTO.isProducesMilk());
            animal.setBirthDate(updateDTO.getBirthDate());

            return animalRepository.save(animal);
        } else  {
            throw new IllegalArgumentException("User not found for update.");
        }
    }
    public boolean isAnimalExist(String animalId) {
        return animalRepository.existsById(animalId);
    }

    public List<Animal> searchAnimals(String query, String userId) {
        String searchQuery = query.toLowerCase();
        List<Animal> userAnimals = animalRepository.findByOwnerId(userId);
        return userAnimals.stream()
                .filter(animal ->
                        animal.getId().toLowerCase().contains(searchQuery) ||
                                (animal.getSpecies() != null &&
                                        animal.getSpecies().toLowerCase().contains(searchQuery)) ||
                                (animal.getBirthDate() != null &&
                                        animal.getBirthDate().toLowerCase().contains(searchQuery))
                )
                .collect(Collectors.toList());
    }
    // Delete an animal
    public void deleteAnimal(String id) {
        animalRepository.deleteById(id);
    }
public void deleteAllAnimals(List<Animal> animals)
{
    animalRepository.deleteAll(animals);
}
    public List<Animal> getAnimalByUserId(String id)
    {
       return animalRepository.findByOwnerId(id);
    }
    public List<Animal> getAnimalsByIds(List<String> animalIds) {
        if (animalIds == null || animalIds.isEmpty()) {
            return List.of();
        }
        List<Animal> animals = animalRepository.findAnimalsByIds(animalIds);
        return animals;
    }


}

