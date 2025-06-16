package com.example.farmerapp;

import com.example.farmerapp.dto.*;
import com.example.farmerapp.models.AnimalEvent;

import java.util.HashMap;
import java.util.Map;

public class AnimalEventMapper {

    public static AnimalEvent fromDto(String animalId, BaseAnimalEventDTO dto) {
        AnimalEvent event = new AnimalEvent();
        event.setAnimalId(animalId);
        event.setEventType(dto.getEventType());
        event.setEventDate(dto.getEventDate());
        Map<String, Object> details = new HashMap<>();
        switch (dto.getEventType()) {
            case "vaccination" -> {
                VaccinationEventDTO v = (VaccinationEventDTO) dto;
                details.put("vaccineName", v.getVaccineName());
                details.put("dosage", v.getDosage());
            }
            case "sickness" -> {
                SicknessEventDTO s = (SicknessEventDTO) dto;
                details.put("diagnosis", s.getDiagnosis());
                details.put("treatment", s.getTreatment());
            }
            case "birth" -> {
                BirthEventDTO b = (BirthEventDTO) dto;
                details.put("calfGender", b.getCalfGender());
                details.put("notes", b.getNotes());
            }
            case "death" -> {
                DeathEventDTO d = (DeathEventDTO) dto;
                details.put("causeOfDeath", d.getCauseOfDeath());
                details.put("notes", d.getNotes());
            }
        }
        event.setDetails(details);
        return event;
    }
}