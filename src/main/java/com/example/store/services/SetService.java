package com.example.store.services;

import com.example.store.model.entities.Item;
import com.example.store.model.entities.ItemSet;
import com.example.store.repositories.ItemRepository;
import com.example.store.repositories.SetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetService {

    @Autowired
    private SetRepository setRepository;
    @Autowired
    private ItemRepository itemRepository;

    // TODO add tests

    public List<Integer> getSets(Item item) {
        List<ItemSet> setList = setRepository.findSetsByItem(item);
        return setList.stream().map(itemSet -> itemSet.getSet().getId()).collect(Collectors.toList());
    }

    public void setSets(Item item, List<Integer> idList) {
        if(idList.isEmpty()) return;
        idList.forEach(id -> addSet(item, id));
    }

    protected void addSet(Item item, int id) {
        ItemSet itemSet = new ItemSet();
        itemSet.setItem(item);
        itemSet.setSet(itemRepository.getById(id));
        setRepository.save(itemSet);
    }

    public void updateSets(Item item, List<Integer> idList) {
        List<ItemSet> currentSetList = setRepository.findSetsByItem(item);
        if(currentSetList.isEmpty() && idList.isEmpty()) return;
        List<Integer> currentIdList = currentSetList.stream()
                .map(itemSet -> itemSet.getSet().getId())
                .collect(Collectors.toList());
        if(currentIdList.size() == idList.size() && currentIdList.containsAll(idList)) return;
        currentSetList.forEach(this::deleteSet);
        idList.forEach(id -> addSet(item, id));
    }

    protected void deleteSet(ItemSet itemSet) {
        setRepository.delete(itemSet);
    }

}
