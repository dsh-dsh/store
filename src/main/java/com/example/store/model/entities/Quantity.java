package com.example.store.model.entities;

import com.example.store.model.enums.QuantityType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.expression.spel.ast.QualifiedIdentifier;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "periodic_quantity")
public class Quantity {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    @Column(name = "data")
    private LocalDate date;

    private float quantity;

    @Enumerated(EnumType.STRING)
    private QuantityType type;

}
