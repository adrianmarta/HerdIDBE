package com.example.farmerapp.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SicknessEventDTO implements BaseAnimalEventDTO {
    private String eventType;
    private LocalDate eventDate;
    private String diagnosis;
    private String treatment;
}
