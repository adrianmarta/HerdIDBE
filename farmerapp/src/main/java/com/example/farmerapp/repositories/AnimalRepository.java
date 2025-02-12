package com.example.farmerapp.repositories;



import com.example.farmerapp.models.Animal;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface AnimalRepository extends MongoRepository<Animal, String> {
    // You can add custom query methods here if needed
    List<Animal> findByOwnerId(String ownerId);
    List<Animal> findAllById(List<String> ids);

    @Query("{ '_id': { $in: ?0 } }")
    List<Animal> findAnimalsByIds(List<String> ids);
}

