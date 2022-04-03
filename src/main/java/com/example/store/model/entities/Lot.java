package com.example.store.model.entities;

import com.example.store.model.entities.documents.ItemDoc;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@EqualsAndHashCode(of = {"item", "lotTime"})
public class Lot implements Comparable{

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE
            , generator = "lotMoveSequence")
    @SequenceGenerator(name = "lotMoveSequence"
            , sequenceName = "LOT_MOVEMENT_SEQ")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "document_id")
    private ItemDoc document;  // TODO is this field necessary

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(name = "lot_time")
    private LocalDateTime lotTime;

    private float quantity;
    private float price;

    public Lot(ItemDoc document, Item item, LocalDateTime lotTime, float quantity, float price) {
        this.document = document;
        this.item = item;
        this.lotTime = lotTime;
        this.quantity = quantity;
        this.price = price;
    }

    @Override
    public int compareTo(@NotNull Object o) {
        LocalDateTime current = this.getLotTime();
        LocalDateTime other = ((Lot)o).getLotTime();
        return current.compareTo(other);
    }
}
