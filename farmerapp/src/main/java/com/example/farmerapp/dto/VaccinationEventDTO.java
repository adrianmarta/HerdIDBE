// VaccinationEventDTO.java
package com.example.farmerapp.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class VaccinationEventDTO implements BaseAnimalEventDTO {
    private String eventType;
    private LocalDate eventDate;
    private String vaccineName;
    private String dosage;
}
