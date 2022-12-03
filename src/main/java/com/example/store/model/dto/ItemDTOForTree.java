package com.example.store.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.TreeSet;

@Setter
@Getter
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemDTOForTree implements  Comparable<ItemDTOForTree>{

    private String key;
    private String label;
    private String data;
    private String icon;

    @JsonProperty("parent_id")
    private int parentId;

    @JsonProperty("is_node")
    private boolean isNode;

    private Set<ItemDTOForTree> children = new TreeSet<>();

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

    @Override
    public int  compareTo(@NotNull ItemDTOForTree other) {
        int compareChildren = 0;
        if(!this.children.isEmpty() && other.children.isEmpty()) compareChildren = -1;
        else if(this.children.isEmpty() && !other.children.isEmpty()) compareChildren = 1;
        return compareChildren != 0 ? compareChildren : this.label.compareToIgnoreCase(other.label);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemDTOForTree)) return false;
        ItemDTOForTree that = (ItemDTOForTree) o;
        if (!label.equals(that.label)) return false;
        return children.equals(that.children);
    }

    @Override
    public int hashCode() {
        int result = label.hashCode();
        result = 31 * result + children.hashCode();
        return result;
    }
}
