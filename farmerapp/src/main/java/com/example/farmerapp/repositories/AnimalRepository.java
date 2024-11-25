package com.example.farmerapp.repositories;



import com.example.farmerapp.models.Animal;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AnimalRepository extends MongoRepository<Animal, String> {
    // You can add custom query methods here if needed
}

