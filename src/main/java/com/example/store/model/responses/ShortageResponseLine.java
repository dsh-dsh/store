package com.example.store.model.responses;

import java.math.BigDecimal;

public class ShortageResponseLine {
    int itemId;
    String itemName;
    BigDecimal value;
    String unit;

    public ShortageResponseLine(int itemId, String itemName, BigDecimal value, String unit) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.value = value;
        this.unit = unit;
    }

    public int getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public BigDecimal getValue() {
        return value;
    }

    public String getUnit() {
        return unit;
    }
}
