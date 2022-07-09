package com.example.store.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemDTOForTree{

    private String key;
    private String label;
    private String data;
    private String icon;

    @JsonProperty("parent_id")
    private int parentId;

    @JsonProperty("is_node")
    private boolean isNode;

    private List<ItemDTOForTree> children = new ArrayList<>();

    public void setIcon() {
        if(children.isEmpty()) this.icon = ItemDTOForTree.Icons.ITEM.value;
        else this.icon = ItemDTOForTree.Icons.FOLDER.value;
    }

    enum Icons {
        FOLDER("pi pi-fw pi-folder"),
        ITEM("pi pi-fw pi-inbox");
        String value;
        Icons(String value) {
            this.value = value;
        }
    }

}
