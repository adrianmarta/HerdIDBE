package com.example.farmerapp.models;

import jakarta.validation.constraints.NotNull;
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
    @NotNull
    private String senderId;
    @NotNull
    private String receiverId;
    @NotNull
    private List<String> animalIds;
    @NotNull
    private LocalDateTime transferDate;
    private TransferStatus status;
} 