package com.example.farmerapp.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SalePostRequest {
    private String title;
    private String description;
    private double price;
    private List<String> animals;
    private int numberofAnimals;// List of animal IDs from the frontend
}
