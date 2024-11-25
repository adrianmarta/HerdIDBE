package com.example.farmerapp.models;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.YearMonth;

@Getter
@Document(collection = "animals") // MongoDB collection name
public class Animal {

    // Getters and Setters
    @Id
    @Pattern(regexp = "^RO\\d{10}$", message = "ID must start with 'RO' followed by a 10-digit number")
    private String id; // Animal ID, which should be a 10-digit Romanian ID

    private String gender;

    private YearMonth birthDate; // Stores month and year of birth

    // Default constructor (required by MongoDB)
    public Animal() {}

    // Constructor with parameters
    public Animal(String id, String gender, YearMonth birthDate) {
        this.id = id;
        this.gender = gender;
        this.birthDate = birthDate;
    }

    public void setId(String id) {
        if (id != null && id.length() == 10) {  // Ensure ID is 10 digits long
            this.id = id;
        } else {
            throw new IllegalArgumentException("ID must be 10 digits.");
        }
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setBirthDate(YearMonth birthDate) {
        this.birthDate = birthDate;
    }

}
