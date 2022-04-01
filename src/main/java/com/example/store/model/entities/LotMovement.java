package com.example.store.model.entities;

import com.example.store.model.entities.documents.ItemDoc;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
//@EqualsAndHashCode(of = {"name", "regTime"})
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
    private ItemDoc doc;

    @Column(name = "movement_date")
    private LocalDate movementDate;

    private float quantity;
    private float amount;

}
