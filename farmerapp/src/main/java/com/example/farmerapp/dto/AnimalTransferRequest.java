package com.example.farmerapp.dto;

import lombok.Data;
import java.util.List;

@Data
public class AnimalTransferRequest {
    private String receiverId;
    private List<String> animalIds;
} 