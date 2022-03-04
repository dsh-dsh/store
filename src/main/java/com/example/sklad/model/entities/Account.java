package com.example.sklad.model.entities;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Account {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bankName;

    private int bankNumber;

    private String accountNumber;

    private String corAccountNumber;

    private Company company;
}
