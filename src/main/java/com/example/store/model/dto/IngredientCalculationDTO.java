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
public class IngredientCalculationDTO {
    @JsonProperty("item_name")
    private String itemName;
    @JsonProperty("cost_price")
    private float costPrice;
    private float net;
    private float gross;
    private float amount;

    public IngredientCalculationDTO(String itemName, float costPrice, float net, float gross, float amount) {
        this.itemName = itemName;
        this.costPrice = costPrice;
        this.net = net;
        this.gross = gross;
        this.amount = amount;
    }
}
