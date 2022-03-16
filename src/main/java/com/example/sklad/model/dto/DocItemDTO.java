package com.example.sklad.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocItemDTO {

    @JsonProperty("document_id")
    private int documentId;

    @JsonProperty("item_id")
    private int itemId;

    private float quantity;

    @JsonProperty("quantity_fact")
    private float quantityFact = 0.00f;

    private float price;

    private float discount;

}
