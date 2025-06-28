package com.example.farmerapp.dto;

import java.time.LocalDate;

public interface BaseAnimalEventDTO {
    String getEventType();
    LocalDate getEventDate();
}
