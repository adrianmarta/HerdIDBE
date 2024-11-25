package com.example.farmerapp.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Document(collection = "users")
public class User {
    @Id
    @Pattern(regexp = "\\d{10}", message = "ID must be a 10-digit number")
    private String id;
    @NotNull
    @Pattern(regexp = "\\d{10}", message = "ID must be a 10-digit number")
    private String Cnp;

    @NotNull
    @Size(min = 1, max = 100)
    private String name;

    @NotNull
    private LocalDate dob;

    @NotNull
    @Size(min = 1, max = 200)
    private String address;

    @Pattern(regexp = "\\d{10}", message = "Phone number must be a 10-digit number")
    private String phoneNumber;
    @JsonManagedReference
    @DBRef // This annotation creates a reference to the Animal collection
    private List<Animal> animals=new ArrayList<>();
}

