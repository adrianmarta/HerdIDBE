// BirthEventDTO.java
package com.example.farmerapp.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BirthEventDTO implements BaseAnimalEventDTO {
    private String eventType;
    private LocalDate eventDate;
    private String calfGender;
    private String notes;
}
