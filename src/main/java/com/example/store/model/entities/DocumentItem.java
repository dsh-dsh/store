package com.example.store.model.entities;

import com.example.store.model.entities.documents.ItemDoc;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@EqualsAndHashCode(of = {"itemDoc", "item"})
public class DocumentItem {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private ItemDoc itemDoc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private float quantity;

    @Column(name = "quantity_fact")
    private float quantityFact;

    private float price;

    private float discount;

}
