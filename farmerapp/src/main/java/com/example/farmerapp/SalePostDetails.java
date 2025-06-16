package com.example.farmerapp;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class SalePostDetails {
    private String title;
    private String description;
    private double price;
    private int numberOfAnimals;
    private List<byte[]> images;
    private String ownerName;
    private String phoneNumber;
    private String location;
    private LocalDateTime creationDate;
    private List<String> species;

    public SalePostDetails(String title, String description, double price, int numberOfAnimals, 
                          List<byte[]> images, String ownerName, String phoneNumber, String location,
                          LocalDateTime creationDate, List<String> species) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.numberOfAnimals = numberOfAnimals;
        this.images = images;
        this.ownerName = ownerName;
        this.phoneNumber = phoneNumber;
        this.location = location;
        this.creationDate = creationDate;
        this.species = species;
    }
}