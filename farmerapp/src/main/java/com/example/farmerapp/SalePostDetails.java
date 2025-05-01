package com.example.farmerapp;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.aggregation.StringOperators;

import java.util.List;
@Getter
@Setter
public class SalePostDetails{
    private String title;
    private String description;
    private double price;
    private int numberofAnimals;
    private List<byte[]> images;
    private String ownerName;
    private String phoneNumber;
    private String location;

    public SalePostDetails(String title, String description, double price, int numberofAnimals, List<byte[]> images, String ownerName,
                           String phoneNumber, String location)
    {
        this.title=title;
        this.description=description;
        this.price=price;
        this.numberofAnimals=numberofAnimals;
        this.images=images;
        this.ownerName=ownerName;
        this.phoneNumber=phoneNumber;
        this.location=location;
    }
}