package com.example.farmerapp.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "interests")
public class Interest {
    @Id
    private String id;

    @DBRef
    private User user;

    @DBRef
    private SalePost salePost;

    public Interest(User user, SalePost salePost) {
        this.user = user;
        this.salePost = salePost;
    }

    public Interest() {}
} 