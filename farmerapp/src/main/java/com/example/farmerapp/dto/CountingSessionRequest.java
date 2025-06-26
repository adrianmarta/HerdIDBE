package com.example.farmerapp.dto;

import lombok.Data;

import java.util.List;

@Data
public class CountingSessionRequest {
    private String name;
    private String folderId;
    private List<String> readAnimalIds;
} 