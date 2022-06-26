package com.example.store.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IngredientDTO {

    private int id;
    private ItemDTOForIngredient parent;
    private ItemDTOForIngredient child;

    @JsonProperty("quantity_list")
    private List<QuantityDTO> quantityList;

    @JsonProperty("is_deleted")
    private boolean isDeleted;

    private String name;

    @JsonProperty("child_id")
    private int childId;

    @JsonProperty("parent_id")
    private int parentId;
    private QuantityDTO netto;
    private QuantityDTO gross;
    private QuantityDTO enable;


}
