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
    @Autowired
    private UserService userService;

    // Create a new animal
    public Animal createAnimal(Animal animal) {
        // Check if animal already exists in any user's list
        if (animalRepository.findById(animal.getId()).isPresent()) {
            throw new IllegalArgumentException("An animal with this ID already exists in the system.");
        }
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
    public Animal changeOwner(String id, String ownerID) {
        Optional<Animal> optionalAnimal = animalRepository.findById(id);
        Optional<User> optionalNewOwner = userService.getUserById(ownerID);
        if (optionalAnimal.isEmpty()) {
            throw new IllegalArgumentException("Animal not found");
        }
        if (optionalNewOwner.isEmpty()) {
            throw new IllegalArgumentException("User not found for changing.");
        }
        Animal animal = optionalAnimal.get();
        User newOwner = optionalNewOwner.get();
        User oldOwner = animal.getOwner();
        if (oldOwner != null) {
            oldOwner.getAnimals().removeIf(a -> a.getId().equals(animal.getId()));
            userService.saveUser(oldOwner);
        }
        newOwner.getAnimals().add(animal);
        animal.setOwner(newOwner);
        animalRepository.save(animal);
        userService.saveUser(newOwner);
        return animal;
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

