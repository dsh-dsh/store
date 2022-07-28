package com.example.store.model.entities;

import com.example.store.model.enums.PriceType;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(of = {"item", "date"})
public class Price {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(name = "price_value")
    private float value;

    @Column(name = "price_date")
    private LocalDate date;

    @Enumerated(value = EnumType.STRING)
    private PriceType priceType;
}
