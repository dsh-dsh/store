package com.example.store.model.entities;

import com.example.store.model.dto.ItemDTOForList;
import com.example.store.model.enums.Unit;
import com.example.store.model.enums.Workshop;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(of = {"name", "regTime"})
public class Item implements EntityInterface{

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String printName;
    private String comment;

    @Column(nullable = false)
    private LocalDateTime regTime;


    private boolean isWeight;
    private boolean isInEmployeeMenu;

    private boolean isNotInPriceList;
    private boolean isAlcohol;

    private boolean isGarnish;
    private boolean isIncludeGarnish;

    private boolean isSauce;
    private boolean isIncludeSauce;

    @Enumerated(EnumType.STRING)
    private Workshop workshop;

    @Enumerated(EnumType.STRING)
    private Unit unit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Item parent;

    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY)
    private List<Price> prices = new ArrayList<>();

    private boolean isDeleted;
    private int number;

    public Item(int id) {
        this.id = id;
    }

    @Override
    public int getParentId() {
        return parent.getId();
    }
}
