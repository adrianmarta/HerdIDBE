package com.example.farmerapp.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "bids")
public class Bid {

    @Id
    private String id;

    @DBRef
    private User bidder;

    @DBRef
    private SalePost salePost;

    private double bidAmount;

    private boolean isWinner = false; // ✅ Track winning bid

    public Bid() {}

    public Bid(User bidder, SalePost salePost, double bidAmount) {
        this.bidder = bidder;
        this.salePost = salePost;
        this.bidAmount = bidAmount;
    }
}

