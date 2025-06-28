package com.example.farmerapp.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Document(collection = "animals")
public class Animal {


    @Id
    @Pattern(regexp = "^RO\\d{10}$", message = "ID must start with 'RO' followed by a 10-digit number")
    private String id;

    private String gender;
    private String species;
    private boolean producesMilk;
    @DBRef
    private List<AnimalEvent> events = new ArrayList<>();
    private String birthDate;
    @JsonBackReference
    @DBRef
    private User owner;


    public Animal() {}


    public Animal(String id, String gender, String birthDate, String species, boolean producesMilk, User owner) {
        this.id = id;
        this.gender = gender;
        this.birthDate = birthDate;
        this.species = species;
        this.producesMilk = producesMilk;
        this.owner = owner;
    }



    public void setGender(String gender) {
        this.gender = gender;
    }



}
