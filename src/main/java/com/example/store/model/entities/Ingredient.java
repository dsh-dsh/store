package com.example.store.model.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = {"parent", "child"})
@Entity
public class Ingredient {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ingredientSequence")
    @SequenceGenerator(name = "ingredientSequence", sequenceName = "INGREDIENT_SEQ")
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id")
    private Item parent;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "child_id")
    private Item child;

    @OneToMany(mappedBy = "ingredient")
    private List<Quantity> quantityList;

    private boolean isDeleted;

    public Ingredient(int id, Item child) {
        this.id = id;
        this.child = child;
    }
}
