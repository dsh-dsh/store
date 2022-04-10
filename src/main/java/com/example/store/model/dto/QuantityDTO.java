package com.example.store.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuantityDTO {

    private int id;
    private String date;
    private float quantity;
    private String type;
    private boolean isDeleted;

}
