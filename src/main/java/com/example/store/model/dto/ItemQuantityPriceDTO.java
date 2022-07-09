package com.example.store.model.dto;

import com.example.store.model.entities.Item;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemQuantityPriceDTO {
    private Item item;
    private float quantity;
    private float price;
}
