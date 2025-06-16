package com.example.farmerapp.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnimalUpdateDTO {
    private String gender;
    private String species;
    private boolean producesMilk;
    private String birthDate;
}
