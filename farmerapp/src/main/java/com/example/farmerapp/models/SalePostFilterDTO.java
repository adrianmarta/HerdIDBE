package com.example.farmerapp.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalePostFilterDTO {
    private Double minPrice;
    private Double maxPrice;
    private String location;
    private String species;
    private String userId; // For excluding user's own posts
} 