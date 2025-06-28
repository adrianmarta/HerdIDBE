package com.example.farmerapp.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Document(collection = "folders")
public class Folder {

    @Id
    private String id;

    @NotNull
    private String name;

    @JsonManagedReference
    @DBRef
    private User owner;

    @JsonManagedReference
    @DBRef
    private List<Animal> animals = new ArrayList<>();


    public Folder() {}


}
