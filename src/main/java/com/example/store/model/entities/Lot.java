package com.example.store.model.entities;

import com.example.store.model.entities.documents.ItemDoc;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@EqualsAndHashCode(of = {"documentItem", "lotTime"})
public class Lot implements Comparable<Lot> {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE
            , generator = "lotMoveSequence")
    @SequenceGenerator(name = "lotMoveSequence"
            , sequenceName = "LOT_MOVEMENT_SEQ")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "document_item_id")
    private DocumentItem documentItem;

    @Column(name = "lot_time")
    private LocalDateTime lotTime;

    public Lot(ItemDoc document, Item item, LocalDateTime lotTime, float quantity, float price) {
        this.documentItem = new DocumentItem(document, item, quantity, price);
        this.lotTime = lotTime;
    }

    public Lot(DocumentItem documentItem, LocalDateTime lotTime) {
        this.documentItem = documentItem;
        this.lotTime = lotTime;
    }

    @Override
    public int compareTo(Lot lot) {
        LocalDateTime current = this.getLotTime();
        LocalDateTime other = lot.getLotTime();
        return current.compareTo(other);
    }
}
