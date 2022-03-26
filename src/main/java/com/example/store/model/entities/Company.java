package com.example.store.model.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@EqualsAndHashCode(of = "inn")
public class Company {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @Column(nullable = false)
    private long inn;

    private int kpp;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    private List<Account> accounts = new ArrayList<>();

    private boolean isMine;
}
