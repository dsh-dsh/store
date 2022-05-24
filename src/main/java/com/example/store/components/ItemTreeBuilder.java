package com.example.store.components;

import com.example.store.mappers.ItemMapper;
import com.example.store.model.dto.ItemDTOForTree;
import com.example.store.model.entities.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ItemTreeBuilder {

    @Autowired
    private ItemMapper itemMapper;

    private ItemDTOForTree[] itemArray;
    private List<ItemDTOForTree> itemList;

    public List<ItemDTOForTree> getItemTree(List<Item> list) {
        fillItemArray(list);
        fillChildren();

        itemList = Arrays.stream(itemArray)
                .filter(Objects::nonNull)
                .filter(dto -> dto.getParentId() == 0).collect(Collectors.toList());
        
        setKeys(itemList, "");

        return itemList;
    }
    
    private void setKeys(List<ItemDTOForTree> list, String key) {
        int i = 0;
        for(ItemDTOForTree item : list) {
            if(!item.getChildren().isEmpty()) {
                setKeys(item.getChildren(), key + i + "-");
            }
            item.setKey(key + i);
            item.setIcon();
            i++;
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

    private void fillItemArray(List<Item> list) {
        int arraySize = list.get(list.size()-1).getId();
        itemArray = new ItemDTOForTree[arraySize+1];
        for (Item item : list) {
            ItemDTOForTree dto = itemMapper.mapToDTOForTree(item);
            itemArray[item.getId()] = dto;
        }
    }

}
