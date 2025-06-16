package com.example.farmerapp.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class DeathEventDTO implements BaseAnimalEventDTO {
    private String eventType;
    private LocalDate eventDate;
    private String causeOfDeath;
    private String notes;
} 