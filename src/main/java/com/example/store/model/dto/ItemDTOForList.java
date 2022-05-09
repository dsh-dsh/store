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

    private String key;
    private String label;
    private String data;
    private String icon;

    private int id;
    private String name;

    @JsonProperty("parent_id")
    private int parentId;

    private List<ItemDTOForList> children;

    public ItemDTOForList getDTOForList(List<ItemDTOForList> items) {
        ItemDTOForList dto = new ItemDTOForList();
        dto.setId(getId()); dto.setData(String.valueOf(getId()));
        dto.setName(getName()); dto.setLabel(getName());
        List<ItemDTOForList> childrenList = items.stream()
                .filter(item -> {
                    int parentId = item.getParentId();
                    return parentId == getId();
                })
                .map(item -> item.getDTOForList(items))
                .collect(Collectors.toList());
        dto.setChildren(childrenList);

        if(childrenList.isEmpty()) dto.setIcon(Icons.ITEM.value);
        else dto.setIcon(Icons.FOLDER.value);

        return dto;
    }

     enum Icons {
        FOLDER("pi pi-fw pi-folder-open"),
        ITEM("pi pi-fw pi-inbox");
        String value;
        Icons(String value) {
            this.value = value;
        }
    }
}
