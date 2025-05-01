package com.example.farmerapp;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BidRequest {
    private String salePostID;
    private double bidAmount;
    public BidRequest(String salePostID, double bidAmount)
    {
        this.bidAmount=bidAmount;
        this.salePostID =salePostID;
    }
}
