package com.example.testWork.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String email;
    private String password;
    private String phone;
    private double balance;
    private String firstName;
    private String lastName;
    private String fathersName;
    private String birthDate;

    public User() {
        balance = 100.0d;
    }
}
