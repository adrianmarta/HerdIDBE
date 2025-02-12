package com.example.farmerapp.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@Document(collection = "sale_posts")
public class SalePost {

    @Id
    private String id;
    private String title;
    private String description;
    private double price;

    @DBRef
    private List<Animal> animals;

    private int numberOfAnimals;

    @DBRef
    private User owner; // Reference to the user

    private List<byte[]> images; // Store actual image data as byte arrays

    public SalePost() {}

    public SalePost(String title, String description, double price, List<Animal> animals, int numberOfAnimals,
                    User owner, List<byte[]> images) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.animals = animals;
        this.numberOfAnimals = numberOfAnimals;
        this.owner = owner;
        this.images = images;
    }
}
