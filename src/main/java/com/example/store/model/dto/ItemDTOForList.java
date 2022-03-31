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

    private List<ItemDTOForList> children;

    public ItemDTOForList getDTOForList(List<ItemDTOForList> items) {
        ItemDTOForList dto = new ItemDTOForList();
        dto.setId(getId());
        dto.setName(getName());
        List<ItemDTOForList> children = items.stream()
                .filter(item -> {
                    int parentId = item.getParentId();
                    return parentId == getId();
                })
                .map(item -> item.getDTOForList(items))
                .collect(Collectors.toList());
        dto.setChildren(children);
        return dto;
    }
}
