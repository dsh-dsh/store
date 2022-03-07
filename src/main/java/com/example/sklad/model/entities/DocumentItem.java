package com.example.sklad.model.entities;

import com.example.sklad.model.entities.documents.ItemMoveDoc;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class DocumentItem {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "document_id")
    private ItemMoveDoc itemMoveDoc;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id")
    private Item item;

    private double quantity;

    private double price;

}
