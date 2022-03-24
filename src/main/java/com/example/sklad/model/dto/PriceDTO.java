package com.example.sklad.model.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PriceDTO {
    private int id;
    private float value;
    private String date;
    private String type;
}
