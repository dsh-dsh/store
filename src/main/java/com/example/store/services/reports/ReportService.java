package com.example.store.services.reports;

import com.example.store.model.entities.Item;
import com.example.store.services.ItemService;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ReportService {

    @Autowired
    private ItemService itemService;

    @Nullable
    protected Item getItem(int itemId) {
        Item item = null;
        if(itemId > 0) {
            item = itemService.getItemById(itemId);
        }
        return item;
    }

    protected List<Item> getItemList(Item item) {
        if(item == null) {
            return itemService.getAllItems();
        } else if(item.isNode()) {
            return itemService.getByParent(item);
        } else {
            return List.of(item);
        }
    }
}
