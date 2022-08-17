package com.example.store.model.dto;

import com.example.store.model.entities.Item;
import com.example.store.utils.Util;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemQuantityPriceDTO {
    private Item item;
    private float quantity;
    private float price;

    public ItemQuantityPriceDTO(Item item, float quantity, float price) {
        this.item = item;
        this.quantity = Util.floorValue(quantity, 1000);
        this.price = Util.floorValue(price, 100);
    }
}
