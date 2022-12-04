package com.example.store.model.dto.documents;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocToPaymentDTO {

    private int id;

    private long number;

    private String supplierDocNumber;

    @JsonProperty("doc_type")
    private String docType;

    @JsonProperty("date_time")
    private long dateTime;

    private String supplier;

    private double amount;

    @JsonProperty("is_payed")
    private boolean isPayed;

}
