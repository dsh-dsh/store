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
@EqualsAndHashCode(of = {"lot", "movementTime"})
public class LotMovement {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "lotSequence")
    @SequenceGenerator(name = "lotSequence",
            sequenceName = "LOT_SEQ")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lot_id")
    private Lot lot;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "storage_id")
    private Storage storage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private ItemDoc document;

    @Column(name = "movement_time")
    private LocalDateTime movementTime;

    private float quantity;


    public LotMovement(Lot lot, ItemDoc document, LocalDateTime movementTime, Storage storage, float quantity) {
        this.lot = lot;
        this.document = document;
        this.movementTime = movementTime;
        this.storage = storage;
        this.quantity = quantity;
    }
}
