package com.example.farmerapp.models;



import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserDTO {
    private String name;
    private LocalDate dob;
    private String address;
    private String phoneNumber;
}

