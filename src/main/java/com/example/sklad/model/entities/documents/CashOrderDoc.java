package com.example.sklad.model.entities.documents;

import com.example.sklad.model.entities.Company;
import com.example.sklad.model.enums.PaymentType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class CashOrderDoc extends Document  implements DocInterface {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id")
    private Company company;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    private double amount;

}
