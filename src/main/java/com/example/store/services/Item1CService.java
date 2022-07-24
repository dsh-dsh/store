package com.example.store.services;

import com.example.store.exceptions.BadRequestException;
import com.example.store.model.dto.Item1CDTO;
import com.example.store.model.dto.ItemDTO;
import com.example.store.model.dto.requests.ItemList1CRequestDTO;
import com.example.store.model.entities.Item;
import com.example.store.model.enums.Unit;
import com.example.store.model.enums.Workshop;
import com.example.store.utils.Constants;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Setter
@Service
public class Item1CService extends ItemService{

    @Autowired
    private Ingredient1CService ingredient1CService;

    private LocalDate date;

    public void setItemsFrom1C(ItemList1CRequestDTO itemList1CRequestDTO) {
        date = LocalDate.now();
        List<Item1CDTO> dtoList = itemList1CRequestDTO.getItem1CDTOList();
        dtoList.sort(Comparator.comparing(Item1CDTO::getNumber));

        setItemsRecursive(new ArrayList<>(dtoList));

        dtoList.forEach(this::setIngredientsAndSets);
    }

    private void setItemsRecursive(List<Item1CDTO> dtoList) {
        if(!dtoList.isEmpty()) {
            Iterator<Item1CDTO> iterator = dtoList.iterator();
            boolean interrupt = true;
            while (iterator.hasNext()) {
                Item1CDTO dto = iterator.next();
                if (itemRepository.existsByNumber(dto.getParentNumber())) {
                    setItem(dto);
                    iterator.remove();
                    interrupt = false;
                }
            }
            if(interrupt) return; // where is not any parent node, so infinity loop
            setItemsRecursive(dtoList);
        }
    }

    public void setItem(Item1CDTO dto) {
        Optional<Item> itemOptional = findItemByNumber(dto.getNumber());
        if(itemOptional.isPresent()) {
            updateItem(itemOptional.get(), dto, date);
        } else {
            setNewItem(dto);
        }
    }

    public void setIngredientsAndSets(Item1CDTO dto) {
        Optional<Item> itemOptional = findItemByNumber(dto.getNumber());
        itemOptional.ifPresent(item -> updateIngredientAndSet(item, dto));
    }

    public void setNewItem(Item1CDTO item1CDTO) {
        Item parent = findItemByNumber(item1CDTO.getParentNumber())
                .orElseThrow(() -> new BadRequestException(Constants.NO_SUCH_ITEM_MESSAGE));
        Item item = itemMapper.mapToItem(item1CDTO);
        item.setParent(parent);
        itemRepository.save(item);
        priceService.addPrices(item, item1CDTO);
    }

    public void updateItem(Item item, Item1CDTO itemDTO, LocalDate date) {
        updateItemFields(item, itemDTO);
        itemRepository.save(item);
        priceService.updateItemPrices(item, itemDTO.getPrices(), date);
    }

    public void updateIngredientAndSet(Item item, Item1CDTO itemDTO) {
        setService.updateSets(item, itemDTO.getSets());
        ingredient1CService.updateIngredients(item, itemDTO.getIngredients());
    }

    @Override
    protected void updateItemFields(Item item, ItemDTO dto) {
        item.setName(dto.getName());
        item.setPrintName(dto.getPrintName());
        item.setWeight(dto.isWeight());
        item.setNotInEmployeeMenu(dto.isNotInEmployeeMenu());
        item.setAlcohol(dto.isAlcohol());
        item.setGarnish(dto.isGarnish());
        item.setIncludeGarnish(dto.isIncludeGarnish());
        item.setSauce(dto.isSauce());
        item.setIncludeSauce(dto.isIncludeSauce());
        item.setWorkshop(Workshop.valueOf(dto.getWorkshop().getCode()));
        item.setUnit(Unit.valueOf(dto.getUnit().getCode()));
        Item parent = findItemByNumber(((Item1CDTO)dto).getParentNumber())
                .orElseThrow(() -> new BadRequestException(Constants.NO_SUCH_ITEM_MESSAGE));
        item.setParent(parent);
    }

}
