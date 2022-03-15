package com.example.sklad.model.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@EqualsAndHashCode(of = {"bankNumber", "accountNumber"})
public class Account {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String bankName;

    private int bankNumber;

    private String accountNumber;

    private String corAccountNumber;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

}
