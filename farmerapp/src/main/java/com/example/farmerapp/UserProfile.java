package com.example.farmerapp;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
public class UserProfile {
    private String id;
    private String name;
    private String dob;
    private String address;
    private String phoneNumber;

    // Constructor
    public UserProfile(String id, String name, String dob, String address,String phoneNumber) {
        this.id = id;
        this.name = name;
        this.dob = dob;
        this.address = address;
        this.phoneNumber=phoneNumber;
    }

    // Getters and setters
    // ...
}
