package com.example.farmerapp.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private User owner;

    private List<byte[]> images;

    @DBRef
    private List<Bid> bids;

    private LocalDateTime expiryDate;

    @DBRef
    private Bid winnerBid; // ✅ Track the winning bid

    private boolean isSold = false; // ✅ Mark as sold when a bid is approved



    public SalePost(String title, String description, double price, List<Animal> animals, int numberOfAnimals,
                    User owner, List<byte[]> images, LocalDateTime expiryDate) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.animals = animals;
        this.numberOfAnimals = numberOfAnimals;
        this.owner = owner;
        this.images = images;
        this.expiryDate = expiryDate;
        this.bids = new ArrayList<>();
    }
}


