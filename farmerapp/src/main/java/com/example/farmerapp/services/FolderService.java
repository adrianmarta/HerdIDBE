package com.example.farmerapp.services;

import com.example.farmerapp.models.Folder;
import com.example.farmerapp.repositories.FolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
}
