package com.example.farmerapp.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "counting_sessions")
public class CountingSession {
    @Id
    private String id;
    private String name;
    private String ownerId;
    private String folderId;
    private List<String> readAnimalIds;
    private LocalDateTime readDate;
} 