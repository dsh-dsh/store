package com.example.store.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemDTO {

    private int id;
    private String name;

    @JsonProperty("print_name")
    private String printName;

    @JsonProperty("reg_time")
    private long regTime;

    @JsonProperty("is_weight")
    private boolean isWeight;

    @JsonProperty("is_in_employee_menu")
    private boolean isInEmployeeMenu;

    @JsonProperty("is_alcohol")
    private boolean isAlcohol;

    @JsonProperty("is_garnish")
    private boolean isGarnish;

    @JsonProperty("is_include_garnish")
    private boolean isIncludeGarnish;

    @JsonProperty("is_sauce")
    private boolean isSauce;

    @JsonProperty("is_include_sauce")
    private boolean isIncludeSauce;

    private String workshop;

    private String unit;

    @JsonProperty("parent_id")
    private int parentId;

    private List<PriceDTO> prices;

    @JsonProperty("in_sets")
    private List<Integer> inSets;
}
