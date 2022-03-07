package com.example.sklad.model.entities;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Project {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

}
