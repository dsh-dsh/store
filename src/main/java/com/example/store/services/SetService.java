package com.example.store.services;

import com.example.store.model.entities.Item;
import com.example.store.model.entities.ItemSet;
import com.example.store.repositories.SetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class SetService {

    @Autowired
    private SetRepository setRepository;

    public List<Integer> getSets(Item item) {
        List<ItemSet> setList = setRepository.findSetsByItem(item);
        return setList.stream().map(itemSet -> itemSet.getSet().getId()).collect(Collectors.toList());
    }

}
