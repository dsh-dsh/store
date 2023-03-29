package com.example.store.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Ingredient1CDTO extends IngredientDTO {

    @JsonProperty("child_number")
    private int childNumber;

    @JsonProperty("parent_number")
    private int parentNumber;

}
