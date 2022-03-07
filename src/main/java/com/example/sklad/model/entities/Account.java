package com.example.sklad.model.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
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

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;
}
