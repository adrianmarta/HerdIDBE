package com.example.farmerapp.repositories;

import com.example.farmerapp.models.Folder;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface FolderRepository extends MongoRepository<Folder, String> {
    Optional<Folder> findByNameAndOwnerId(String name, String ownerId);  // Find a folder by name and owner
    List<Folder> findByOwnerId(String ownerId);  // Find all folders by owner ID
}
