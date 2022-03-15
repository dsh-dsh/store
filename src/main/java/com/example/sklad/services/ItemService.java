package com.example.sklad.services;

import com.example.sklad.model.entities.Item;
import com.example.sklad.repositories.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    public Item getItemById(int id) {
        return itemRepository.getById(id);
    }

}
