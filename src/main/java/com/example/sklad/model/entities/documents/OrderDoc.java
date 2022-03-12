package com.example.sklad.model.entities.documents;

import com.example.sklad.model.enums.PaymentType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class OrderDoc extends Document  implements DocInterface {

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    private double amount;

    private double tax;

}
