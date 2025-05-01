package com.example.farmerapp;

import lombok.Getter;

@Getter
public class FolderRequest {
    // Getters and setters
    private String name;  // Folder name
    private String ownerId;  // Owner ID

    public void setName(String name) {
        this.name = name;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
}
