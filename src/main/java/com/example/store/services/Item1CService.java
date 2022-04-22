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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Setter
@Service
public class Item1CService extends ItemService{

    private LocalDate date;

    public void setItems(ItemList1CRequestDTO itemList1CRequestDTO) {
        date = LocalDate.now();
        List<Item1CDTO> dtoList = itemList1CRequestDTO.getItem1CDTOList();
        dtoList.forEach(this::setItem);
    }

    public void setItem(Item1CDTO dto) {
        Optional<Item> itemOptional = findItemByNumber(dto.getNumber());
        if(itemOptional.isPresent()) {
            updateItem(itemOptional.get(), dto, date);
        } else {
            setNewItem(dto);
        }
    }

    public Item setNewItem(Item1CDTO item1CDTO) {
        Item parent = findItemByNumber(item1CDTO.getParentNumber())
                .orElseThrow(() -> new BadRequestException(Constants.NO_SUCH_ITEM_MESSAGE));
        Item item = itemMapper.mapToItem(item1CDTO);
        item.setParent(parent);
        itemRepository.save(item);
        priceService.addPrices(item, item1CDTO);
        setService.setSets(item, item1CDTO.getSets());
        ingredientService.setIngredients(item, item1CDTO.getIngredients());
        return item;
    }

    public void updateItem(Item item, Item1CDTO itemDTO, LocalDate date) {
        updateItemFields(item, itemDTO);
        itemRepository.save(item);
        priceService.updateItemPrices(item, itemDTO.getPrices(), date);
        setService.updateSets(item, itemDTO.getSets());
        ingredientService.updateIngredients(item, itemDTO.getIngredients());
    }

    @Override
    protected void updateItemFields(Item item, ItemDTO dto) {
        item.setName(dto.getName());
        item.setPrintName(dto.getPrintName());
        item.setWeight(dto.isWeight());
        item.setInEmployeeMenu(dto.isInEmployeeMenu());
        item.setAlcohol(dto.isAlcohol());
        item.setGarnish(dto.isGarnish());
        item.setIncludeGarnish(dto.isIncludeGarnish());
        item.setSauce(dto.isSauce());
        item.setIncludeSauce(dto.isIncludeSauce());
        item.setWorkshop(Workshop.valueOf(dto.getWorkshop()));
        item.setUnit(Unit.valueOf(dto.getUnit()));
        Item parent = findItemByNumber(((Item1CDTO)dto).getParentNumber())
                .orElseThrow(() -> new BadRequestException(Constants.NO_SUCH_ITEM_MESSAGE));
        item.setParent(parent);
    }

}
