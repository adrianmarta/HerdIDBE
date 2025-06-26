package com.example.farmerapp.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "animal_transfers")
public class AnimalTransfer {
    @Id
    private String id;
    private String senderId;
    private String receiverId;
    private List<String> animalIds;
    private LocalDateTime transferDate;
    private TransferStatus status;
} 