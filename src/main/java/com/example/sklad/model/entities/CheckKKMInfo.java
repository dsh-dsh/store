package com.example.sklad.model.entities;

import com.example.sklad.model.entities.documents.ItemDoc;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"checkNumber", "cashRegisterNumber"})
@Entity
@Table(name = "check_KKM_info")
public class CheckKKMInfo {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private long checkNumber;
    private String cashRegisterNumber;
    private double amountReceived;
    private int guestNumber;
    private int tableNumber;
    private String waiter;
    private LocalDateTime time;
    private boolean isReturn;
    @Column(name = "is_KKM_checked")
    private boolean isKKMChecked;
    private boolean isPayed;
    private boolean isPayedByCard;
    private boolean isDelivery;

    @ManyToOne
    @JoinColumn(name = "check_id")
    private ItemDoc check;
}

