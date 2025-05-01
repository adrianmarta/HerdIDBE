package com.example.farmerapp.repositories;

import com.example.farmerapp.models.AnimalEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnimalEventRepository extends MongoRepository<AnimalEvent, String> {
    List<AnimalEvent> findByAnimalId(String animalId);

}
