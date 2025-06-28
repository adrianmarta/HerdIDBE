package com.example.farmerapp.controllers;

import com.example.farmerapp.models.FolderRequest;
import com.example.farmerapp.models.Folder;
import com.example.farmerapp.models.Animal;
import com.example.farmerapp.models.User;
import com.example.farmerapp.services.FolderService;
import com.example.farmerapp.services.AnimalService;
import com.example.farmerapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/folders")
public class FolderController {

    @Autowired
    private FolderService folderService;
    @Autowired
    private AnimalService animalService;
    @Autowired
    private UserService userService;

    @GetMapping("/user")
    public ResponseEntity<List<Folder>> getFoldersByToken(
           ) {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Folder> folders = folderService.getFoldersByUserId(userId);
        if (folders.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }
        return ResponseEntity.ok(folders);
    }

    @PutMapping("/{folderId}/remove-animals")
    public ResponseEntity<Folder> removeAnimalsFromFolder(@PathVariable String folderId, @RequestBody List<String> animalIds) {
        Optional<Folder> folderOptional = folderService.getFolderById(folderId);
        if (folderOptional.isPresent()) {
            Folder folder = folderOptional.get();
            List<Animal> updatedAnimals = folder.getAnimals().stream()
                    .filter(animal -> !animalIds.contains(animal.getId()))
                    .collect(Collectors.toList());
            folder.setAnimals(updatedAnimals);
            folderService.updateFolder(folder);
            return ResponseEntity.ok(folder);
        }
        return ResponseEntity.status(404).body(null);
    }

    @PostMapping
    public ResponseEntity<Folder> createFolder(
            @RequestBody FolderRequest folderRequest) {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> ownerOptional = userService.getUserById(userId);
        if (ownerOptional.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }
        if (folderRequest.getName() == null || folderRequest.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        User owner = ownerOptional.get();

        Folder savedFolder = folderService.createFolder(folderRequest,owner);
        return ResponseEntity.ok(savedFolder);
    }

    @GetMapping("/{folderId}/animals")
    public ResponseEntity<?> getAnimalsInFolder(@PathVariable String folderId) {
        String userId = (String) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        java.util.List<com.example.farmerapp.models.Animal> userAnimals = animalService.getAnimalByUserId(userId);
        try {
            java.util.List<com.example.farmerapp.models.Animal> animals = folderService.getAnimalsInFolderForUser(folderId, userId, userAnimals);
            return ResponseEntity.ok(animals);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
    @PutMapping("/{folderId}/add-existing-animal/{animalId}")
    public ResponseEntity<?> addExistingAnimalToFolder(@PathVariable String folderId, @PathVariable String animalId
                                                       ) {
        Optional<Folder> folderOptional = folderService.getFolderById(folderId);
        Optional<Animal> animalOptional = animalService.getAnimalById(animalId);
        if (folderOptional.isPresent() && animalOptional.isPresent()) {
            Folder folder = folderOptional.get();
            Animal animal = animalOptional.get();
            if (folder.getAnimals().contains(animal)) {
                return ResponseEntity.status(400).body("Animal already exists in the folder.");
            }
            folder.getAnimals().add(animal);
            folderService.updateFolder(folder);
            return ResponseEntity.ok(folder);
        }
        return ResponseEntity.status(404).body("Folder or Animal not found.");
    }


    @PutMapping("/{folderId}/add-animals")
    public ResponseEntity<?> addAnimalsToFolder(@PathVariable String folderId, @RequestBody List<String> animalIds) {
        Optional<Folder> folderOptional = folderService.getFolderById(folderId);
        if (folderOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Folder not found.");
        }
        Folder folder = folderOptional.get();
        List<Animal> existingAnimals = folderService.getAnimalsInFolder(folder.getId());
        Set<String> existingAnimalIds = existingAnimals.stream().map(Animal::getId).collect(Collectors.toSet());
        List<String> alreadyAddedAnimalIds = animalIds.stream()
                .filter(existingAnimalIds::contains)
                .toList();
        if (!alreadyAddedAnimalIds.isEmpty()) {
            return ResponseEntity.status(409).body(
                    String.join(", ", alreadyAddedAnimalIds)
            );
        }
        List<Animal> newAnimalsToAdd = animalService.getAnimalsByIds(animalIds);
        folder.getAnimals().addAll(newAnimalsToAdd);
        folderService.updateFolder( folder);
        return ResponseEntity.ok("All selected animals have been successfully added.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<Folder> updateFolder(@PathVariable String id, @RequestBody FolderRequest folderRequest) {
        Optional<Folder> folder=folderService.getFolderById(id);
        return ResponseEntity.ok(folderService.updateFolderName(folderRequest.getName(),folder.get()));
    }
    @GetMapping("/compare/{folderId1}/{folderId2}")
    public ResponseEntity<List<String>> compareFolders(
            @PathVariable String folderId1,
            @PathVariable String folderId2) {
        try {
            List<String> animalIds = folderService.compareFolders(folderId1, folderId2)
                    .stream()
                    .map(Animal::getId)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(animalIds);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(null);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFolder(@PathVariable String id) {
        folderService.deleteFolder(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{folderId}")
    public ResponseEntity<Folder> getFolder(
            @PathVariable String folderId) {
        Optional<Folder> folder = folderService.getFolderById(folderId);
        if (folder == null) {
            return ResponseEntity.status(404).body(null);
        }
        return ResponseEntity.ok(folder.get());
    }
}
