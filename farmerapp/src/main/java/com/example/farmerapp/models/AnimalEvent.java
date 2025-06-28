package com.example.farmerapp.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Map;

@Getter
@Setter
@Document(collection = "animal_events")
public class AnimalEvent {
    @Id
    private String id;

    private String animalId;
    private String eventType;
    private LocalDate eventDate;

    private Map<String, Object> details;

    public AnimalEvent() {}


}
