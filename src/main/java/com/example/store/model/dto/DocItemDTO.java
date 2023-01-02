package com.example.store.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

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

    private String unit;

    private float quantity;

    private float amount;

    @JsonProperty("amount_fact")
    private float amountFact;

    @JsonProperty("quantity_fact")
    private float quantityFact = 0.00f;

    private float price;

    private float discount;

    private List<DocItemDTO> children;

    public DocItemDTO(int documentId, int itemId, String itemName, String unit,
                      float quantity, float quantityFact, float price, float discount, List<DocItemDTO> children) {
        this.documentId = documentId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.unit = unit;
        this.quantity = quantity;
        this.quantityFact = quantityFact;
        this.price = price;
        this.discount = discount;
        this.children = children;
        this.amount = quantity * price;
        this.amountFact = quantityFact * price;
    }
}
