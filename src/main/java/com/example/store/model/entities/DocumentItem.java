package com.example.store.model.entities;

import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.utils.Util;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

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

    @Column(name = "quantity")
    private BigDecimal quantity;

    @Column(name = "quantity_fact")
    private float quantityFact;

    private float price;

    private float discount;


    public DocumentItem(ItemDoc itemDoc, Item item, BigDecimal quantity) {
        this.itemDoc = itemDoc;
        this.item = item;
        this.quantity = quantity;
    }

    public DocumentItem(ItemDoc itemDoc, Item item, BigDecimal quantity, float price) {
        this.itemDoc = itemDoc;
        this.item = item;
        this.quantity = quantity;
        this.price = Util.floorValue(price,2);
    }

    @Override
    public String toString() {
        return "DocumentItem{" +
                ", item=" + item.getName() +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}
