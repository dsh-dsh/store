package com.example.store.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemDTOForList {

    private int id;
    private String name;

    @JsonProperty("parent_id")
    private int parentId;

    @JsonProperty("rest_list")
    private List<RestDTO> restList;

    private float price;
}
