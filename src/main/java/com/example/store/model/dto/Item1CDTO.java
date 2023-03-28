package com.example.store.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Item1CDTO extends ItemDTO {

    @JsonProperty("parent_number")
    private int parentNumber;

    private List<IngredientDTO> ingredients;

    @Override
    public String toString() {
        return "Item1CDTO{" +
                "parentNumber=" + parentNumber + "," +
                super.toString() +
                '}';
    }
}
