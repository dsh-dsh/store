package com.example.sklad.model.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Item {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String printName;

    @Column(nullable = false)
    private LocalDateTime regTime;

    private boolean isWeight;
    private boolean isInEmployeeMenu;

    private boolean isAlcohol;

    private boolean isGarnish;
    private boolean isIncludeGarnish;

    private boolean isSauce;
    private boolean isIncludeSauce;

    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY)
    private List<Price> prices = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "workshop_id")
    private Workshop workshop;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

    @OneToMany()
    @JoinTable(name = "dinners")
    private List<Item> inDinners = new ArrayList<>();

}
