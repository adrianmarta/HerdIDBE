package com.example.farmerapp.services;

import com.example.farmerapp.models.Animal;
import com.example.farmerapp.models.Folder;
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

    public Folder createFolder(Folder folder) {
        return folderRepository.save(folder);
    }

    public List<Folder> getFoldersByUserId(String userId) {
        return folderRepository.findByOwnerId(userId);
    }

    public Optional<Folder> getFolderById(String id) {
        return folderRepository.findById(id);
    }

    public Folder updateFolder(String id, Folder folder) {
        if (folderRepository.existsById(id)) {
            folder.setId(id);
            return folderRepository.save(folder);
        } else {
            throw new IllegalArgumentException("Folder not found for update.");
        }
    }

    public void deleteFolder(String id) {
        folderRepository.deleteById(id);
    }

    public Optional<Folder> findByNameAndOwnerId(String name, String ownerId) {
        return folderRepository.findByNameAndOwnerId(name, ownerId);
    }
    public List<Animal> compareFolders(String folderId1, String folderId2) {
        Optional<Folder> folder1Opt = folderRepository.findById(folderId1);
        Optional<Folder> folder2Opt = folderRepository.findById(folderId2);

        if (folder1Opt.isPresent() && folder2Opt.isPresent()) {
            List<Animal> folder1Animals = folder1Opt.get().getAnimals();
            List<Animal> folder2Animals = folder2Opt.get().getAnimals();

            // Return animals in folder1 that are not in folder2
            return folder1Animals.stream()
                    .filter(animal -> !folder2Animals.contains(animal))
                    .collect(Collectors.toList());
        } else {
            throw new IllegalArgumentException("One or both folders not found.");
        }
    }

}
