package com.example.farmerapp.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.YearMonth;

@Getter
@Setter
@Document(collection = "animals") // MongoDB collection name
public class Animal {

    // Getters and Setters
    @Id
    @Pattern(regexp = "^RO\\d{10}$", message = "ID must start with 'RO' followed by a 10-digit number")
    private String id; // Animal ID, which should be a 10-digit Romanian ID

    private String gender;

    private String birthDate; // Stores month and year of birth
    @JsonBackReference
    @DBRef
    private User owner;

    // Default constructor (required by MongoDB)
    public Animal() {}

    // Constructor with parameters
    public Animal(String id, String gender, String birthDate, User owner) {
        this.id = id;
        this.gender = gender;
        this.birthDate = birthDate;
        this.owner=owner;
    }



    public void setGender(String gender) {
        this.gender = gender;
    }



}
