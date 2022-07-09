package com.example.store.model.dto;

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
public class DocItemDTO {

    @JsonProperty("document_id")
    private int documentId;

    @JsonProperty("item_id")
    private int itemId;

    @JsonProperty("item_name")
    private String itemName;

    private float quantity;

    private float amount;

    @JsonProperty("quantity_fact")
    private float quantityFact = 0.00f;

    private float price;

    private float discount;

}
