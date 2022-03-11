package com.example.sklad.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDTO {

    private int id;

    private int number;

    private Timestamp time;

    @JsonProperty("doc_type")
    private String docType;

    private ProjectDTO project;

    private AuthorDTO author;

    private IndividualDTO individual;

    private CompanyDTO company;

    @JsonProperty("payment_type")
    private String paymentType;

    private double amount;

    @JsonProperty("is_hold")
    private boolean isHold;

}
