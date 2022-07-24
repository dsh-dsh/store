package com.example.store.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemDTO {

    private int id;
    private String name;

    @JsonProperty("print_name")
    private String printName;

    private String comment;

    @JsonProperty("reg_time")
    private long regTime;

    @JsonProperty("is_weight")
    private boolean isWeight;

    @JsonProperty("is_not_in_employee_menu")
    private boolean isNotInEmployeeMenu;

    @JsonProperty("is_alcohol")
    private boolean isAlcohol;

    @JsonProperty("is_not_in_price_list")
    private boolean isNotInPriceList;

    @JsonProperty("is_garnish")
    private boolean isGarnish;

    @JsonProperty("is_include_garnish")
    private boolean isIncludeGarnish;

    @JsonProperty("is_sauce")
    private boolean isSauce;

    @JsonProperty("is_include_sauce")
    private boolean isIncludeSauce;

    private EnumDTO workshop;

    private EnumDTO unit;

    @JsonProperty("parent_id")
    private int parentId;

    private int number;

    @JsonProperty("is_node")
    private boolean isNode;

    private List<PriceDTO> prices;

    private List<Integer> sets = new ArrayList<>();

    private List<IngredientDTO> ingredients;
}
