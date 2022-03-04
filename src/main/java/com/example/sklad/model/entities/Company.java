package com.example.sklad.model.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Company {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int inn;

    private int kpp;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    private List<Account> accounts = new ArrayList<>();

    private boolean isMine;
}
