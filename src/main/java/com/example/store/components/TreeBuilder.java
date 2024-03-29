package com.example.store.components;

import com.example.store.model.dto.ItemDTOForTree;
import com.example.store.model.entities.EntityInterface;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class TreeBuilder <E extends EntityInterface>{

    private ItemDTOForTree[] itemArray;

    public List<ItemDTOForTree> getItemTree(List<E> list) {
        if(list.isEmpty()) return new ArrayList<>();
        fillItemArray(list);
        fillChildren();
        Set<ItemDTOForTree> itemSet = Arrays.stream(itemArray)
                .filter(Objects::nonNull)
                .filter(dto -> dto.getParentId() == 0).collect(Collectors.toCollection(TreeSet::new));
        setKeys(itemSet, "");
        return new ArrayList<>(itemSet);
    }

    private void fillItemArray(List<E> list) {
        int arraySize = list.get(list.size()-1).getId();
        itemArray = new ItemDTOForTree[arraySize+1];
        for (E item : list) {
            ItemDTOForTree dto = mapToDTO(item);
            itemArray[item.getId()] = dto;
        }
    }

    private void fillChildren() {
        for(int i = itemArray.length; i > 0; i--) {
            if(itemArray[i-1] == null) continue;
            int parentIndex = itemArray[i-1].getParentId();
            if(parentIndex != 0) {
                itemArray[parentIndex].getChildren().add(itemArray[i-1]);
            }
        }
    }

    private void setKeys(Set<ItemDTOForTree> set, String key) {
        int i = 0;
        for(ItemDTOForTree item : set) {
            if(!item.getChildren().isEmpty()) {
                setKeys(item.getChildren(), key + i + "-");
            }
            item.setKey(key + i);
            item.setIcon();
            i++;
        }
    }

    private ItemDTOForTree mapToDTO(E item) {
        ItemDTOForTree dto = new ItemDTOForTree();
        dto.setData(String.valueOf(item.getId()));
        dto.setLabel(item.getName());
        dto.setParentId(item.getParentId());
        dto.setNode(item.isNode());
        return dto;
    }

}
