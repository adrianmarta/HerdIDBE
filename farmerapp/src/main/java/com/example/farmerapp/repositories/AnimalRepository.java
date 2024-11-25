package com.example.farmerapp.repositories;



import com.example.farmerapp.models.Animal;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AnimalRepository extends MongoRepository<Animal, String> {
    // You can add custom query methods here if needed
    List<Animal> findByOwnerId(String ownerId);
}

