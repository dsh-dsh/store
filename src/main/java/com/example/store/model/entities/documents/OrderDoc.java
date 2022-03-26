package com.example.store.model.entities.documents;

import com.example.store.model.enums.PaymentType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("2")
public class OrderDoc extends Document  implements DocInterface {

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    private float amount;

    private float tax;

}
