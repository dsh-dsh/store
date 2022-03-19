package com.example.sklad.model.dto.documents;

import com.example.sklad.model.dto.UserDTO;
import com.example.sklad.model.dto.CompanyDTO;
import com.example.sklad.model.dto.ProjectDTO;
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

    private UserDTO author;

    private UserDTO individual;

    private CompanyDTO company;

    @JsonProperty("payment_type")
    private String paymentType;

    private float amount;
    private float tax;

    @JsonProperty("is_hold")
    private boolean isHold;

}
