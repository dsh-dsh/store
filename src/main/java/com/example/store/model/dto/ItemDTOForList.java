package com.example.store.model.dto;

import com.example.store.model.entities.Item;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemDTOForList {

    private int id;
    private String name;

    @JsonProperty("parent_id")
    private int parentId;

    @JsonProperty("rest_list")
    private List<RestDTO> restList;
}
