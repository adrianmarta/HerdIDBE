package com.example.farmerapp.services;

import com.example.farmerapp.models.FolderRequest;
import com.example.farmerapp.models.Animal;
import com.example.farmerapp.models.Folder;
import com.example.farmerapp.models.User;
import com.example.farmerapp.repositories.FolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FolderService {

    @Autowired
    private FolderRepository folderRepository;

    public Folder createFolder(FolderRequest folderRequest, User owner) {
        Folder folder=new Folder();
        folder.setName(folderRequest.getName());
        folder.setOwner(owner);
        return folderRepository.save(folder);
    }

    public List<Folder> getFoldersByUserId(String userId) {
        return folderRepository.findByOwnerId(userId);
    }

    public Optional<Folder> getFolderById(String id) {
        return folderRepository.findById(id);
    }

    public Folder updateFolderName(String name, Folder folder) {
        if (folderRepository.existsById(folder.getId())) {
            folder.setName(name);
            return folderRepository.save(folder);
        } else {
            throw new IllegalArgumentException("Folder not found for update.");
        }
    }
    public Folder updateFolder( Folder folder) {
        if (folderRepository.existsById(folder.getId())) {
            return folderRepository.save(folder);
        } else {
            throw new IllegalArgumentException("Folder not found for update.");
        }
    }

    public void deleteFolder(String id) {
        folderRepository.deleteById(id);
    }
    
    public List<Animal> getAnimalsInFolder(String folderId) {
        Optional<Folder> folderOptional = folderRepository.findById(folderId);
        return folderOptional.map(Folder::getAnimals).orElseThrow(() ->
                new IllegalArgumentException("Folder not found.")
        );
    }
    public List<Animal> compareFolders(String folderId1, String folderId2) {
        Optional<Folder> folder1Opt = folderRepository.findById(folderId1);
        Optional<Folder> folder2Opt = folderRepository.findById(folderId2);

        if (folder1Opt.isPresent() && folder2Opt.isPresent()) {
            List<Animal> folder1Animals = folder1Opt.get().getAnimals();
            List<Animal> folder2Animals = folder2Opt.get().getAnimals();
            return folder1Animals.stream()
                    .filter(animal -> !folder2Animals.contains(animal))
                    .collect(Collectors.toList());
        } else {
            throw new IllegalArgumentException("One or both folders not found.");
        }
    }

    public List<Animal> getAnimalsInFolderForUser(String folderId, String userId, List<Animal> userAnimals) {
        Optional<Folder> folderOptional = folderRepository.findById(folderId);
        if (folderOptional.isEmpty()) {
            throw new IllegalArgumentException("Folder not found.");
        }
        Folder folder = folderOptional.get();
        List<Animal> folderAnimals = folder.getAnimals();
        List<Animal> validAnimals = folderAnimals.stream()
                .filter(animal -> userAnimals.stream().anyMatch(userAnimal -> userAnimal.getId().equals(animal.getId())))
                .collect(Collectors.toList());
        List<Animal> toRemove = folderAnimals.stream()
                .filter(animal -> userAnimals.stream().noneMatch(userAnimal -> userAnimal.getId().equals(animal.getId())))
                .collect(Collectors.toList());
        if (!toRemove.isEmpty()) {
            folder.getAnimals().removeAll(toRemove);
            folderRepository.save(folder);
        }
        return validAnimals;
    }

}
