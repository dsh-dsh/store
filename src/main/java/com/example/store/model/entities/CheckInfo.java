package com.example.store.model.entities;

import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.enums.CheckPaymentType;
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
@Table(name = "check_kkm_info")
public class CheckInfo {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int checkNumber;
    private Long cashRegisterNumber;
    private float amountReceived;
    private int guestNumber;
    private int tableNumber;
    private String waiter;

    @Column(name = "date_time")
    private LocalDateTime dateTime;

    private boolean isReturn;

    @Column(name = "is_KKM_checked")
    private boolean isKKMChecked;

    private boolean isPayed;

    @Deprecated(forRemoval = true)
    private boolean isPayedByCard;

    private boolean isDelivery;

    @Enumerated(EnumType.STRING)
    @Column(name = "check_payment_type")
    private CheckPaymentType checkPaymentType;

    @ManyToOne
    @JoinColumn(name = "check_id")
    private ItemDoc check;
}

