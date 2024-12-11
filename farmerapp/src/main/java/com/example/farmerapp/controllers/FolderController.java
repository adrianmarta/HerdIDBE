package com.example.farmerapp.controllers;

import com.example.farmerapp.FolderRequest;
import com.example.farmerapp.models.Folder;
import com.example.farmerapp.models.Animal;
import com.example.farmerapp.models.User;
import com.example.farmerapp.repositories.UserRepository;
import com.example.farmerapp.services.FolderService;
import com.example.farmerapp.services.AnimalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/folders")
public class FolderController {

    @Autowired
    private FolderService folderService;

    @Autowired
    private AnimalService animalService;

    @Autowired
    private UserRepository userRepository;

    // Get all folders for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Folder>> getFoldersByUserId(@PathVariable String userId) {
        List<Folder> folders = folderService.getFoldersByUserId(userId);
        if (folders.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }
        return ResponseEntity.ok(folders);
    }

    // Create a new folder
    @PostMapping
    public ResponseEntity<Folder> createFolder(@RequestBody FolderRequest folderRequest) {
        Optional<User> ownerOptional = userRepository.findById(folderRequest.getOwnerId());

        if (ownerOptional.isPresent()) {
            User owner = ownerOptional.get();
            Folder folder = new Folder(folderRequest.getName(), owner); // Create the folder with owner
            Folder savedFolder = folderService.createFolder(folder); // Save folder
            return ResponseEntity.ok(savedFolder);
        } else {
            return ResponseEntity.status(404).body(null); // Owner not found
        }
    }

    // Add animals to a folder
    @PutMapping("/{folderId}/add-animals")
    public ResponseEntity<Folder> addAnimalsToFolder(@PathVariable String folderId, @RequestBody List<String> animalIds) {
        Optional<Folder> folderOptional = folderService.getFolderById(folderId);
        if (folderOptional.isPresent()) {
            Folder folder = folderOptional.get();
            List<Animal> animalsToAdd = animalService.getAnimalsByIds(animalIds);  // Fetch animals by their IDs
            folder.getAnimals().addAll(animalsToAdd);
            folderService.updateFolder(folderId, folder);
            return ResponseEntity.ok(folder);
        }
        return ResponseEntity.status(404).body(null);
    }

    // Update a folder
    @PutMapping("/{id}")
    public ResponseEntity<Folder> updateFolder(@PathVariable String id, @RequestBody Folder folder) {
        return ResponseEntity.ok(folderService.updateFolder(id, folder));
    }

    // Delete a folder
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFolder(@PathVariable String id) {
        folderService.deleteFolder(id);
        return ResponseEntity.noContent().build();
    }
}
