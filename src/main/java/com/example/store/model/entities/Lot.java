package com.example.store.model.entities;

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
//@EqualsAndHashCode(of = {"name", "regTime"})
public class Lot {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE
            , generator = "lotMoveSequence")
    @SequenceGenerator(name = "lotMoveSequence"
            , sequenceName = "LOT_MOVEMENT_SEQ")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(name = "lot_time")
    private LocalDateTime lotTime;

}
