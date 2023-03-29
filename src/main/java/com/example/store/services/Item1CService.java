package com.example.store.services;

import com.example.store.exceptions.BadRequestException;
import com.example.store.model.dto.Item1CDTO;
import com.example.store.model.dto.ItemDTO;
import com.example.store.model.dto.requests.IdNumberRequest;
import com.example.store.model.dto.requests.ItemList1CRequestDTO;
import com.example.store.model.entities.Item;
import com.example.store.model.enums.Unit;
import com.example.store.model.enums.Workshop;
import com.example.store.utils.Constants;
import com.example.store.utils.Util;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Setter
@Service
public class Item1CService extends ItemService{

    @Autowired
    private Ingredient1CService ingredient1CService;

    private LocalDate date;

    // todo add tests
    public List<Item1CDTO> getItemDTOList() {
        date = LocalDate.now();
        List<Item> items = itemRepository.findByIntNotNullParent();
        return items.stream()
                .peek(item -> item.setPrices(priceService.getPriceListOfItem(item, date)))
                .map(this::mapToItem1CDTO)
                .collect(Collectors.toList());
    }

    // todo add tests
    protected Item1CDTO mapToItem1CDTO(Item item) {
        Item1CDTO dto = itemMapper.mapTo1CDTO(item);
        dto.setName(Util.encodeStringToNumbers(dto.getName()));
        dto.setPrintName(Util.encodeStringToNumbers(dto.getPrintName()));
        dto.setComment(Util.encodeStringToNumbers(dto.getComment()));
        // с этой строкой обработка в 1С увеличивается до бесконечности
        //dto.setIngredient1CDTOList(ingredient1CService.getIngredient1CDTOList(item, date));
        return dto;
    }

    // todo add tests
    public void setNumber(IdNumberRequest idNumberRequest) {
        Item item = getItemById(idNumberRequest.getId());
        item.setNumber(idNumberRequest.getNumber());
        itemRepository.save(item);
    }

    public void setItemsFrom1C(ItemList1CRequestDTO itemList1CRequestDTO) {
        date = LocalDate.now();
        List<Item1CDTO> dtoList = itemList1CRequestDTO.getItem1CDTOList();
        dtoList.sort(Comparator.comparing(Item1CDTO::getNumber));
        addRootItems(dtoList);
        setItemsRecursive(new ArrayList<>(dtoList));
        dtoList.forEach(this::setIngredientsAndSets);
    }

    protected void addRootItems(List<Item1CDTO> dtoList) {
        dtoList.stream().filter(dto -> dto.getParentNumber() == 0).forEach(this::setItem);
        setNullParentIdFieldsToIntNullInDB();
    }

    protected void setNullParentIdFieldsToIntNullInDB() {
        List<Item> items = itemRepository.findByParent(null);
        items.forEach(item -> itemRepository.setParentIdNotNull(item.getId()));
    }

    private void setItemsRecursive(List<Item1CDTO> dtoList) {
        if(!dtoList.isEmpty()) {
            Iterator<Item1CDTO> iterator = dtoList.iterator();
            boolean interrupt = true;
            while (iterator.hasNext()) {
                Item1CDTO dto = iterator.next();
                int parentNum = dto.getParentNumber();
                if (parentNum > 0 && itemRepository.existsByNumber(parentNum)) {
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
        Item item = itemMapper.mapToItem(item1CDTO);
        if(item1CDTO.getParentNumber() > 0) {
            Item parent = findItemByNumber(item1CDTO.getParentNumber())
                    .orElseThrow(() -> new BadRequestException(
                            Constants.NO_SUCH_ITEM_MESSAGE,
                            this.getClass().getName() + " - setNewItem(Item1CDTO item1CDTO)"));
            item.setParent(parent);
        }
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
        item.setNotInEmployeeMenu(dto.isNotInEmployeeMenu());
        item.setAlcohol(dto.isAlcohol());
        item.setGarnish(dto.isGarnish());
        item.setIncludeGarnish(dto.isIncludeGarnish());
        item.setSauce(dto.isSauce());
        item.setIncludeSauce(dto.isIncludeSauce());
        item.setWorkshop(Workshop.valueOf(dto.getWorkshop().getCode()));
        item.setUnit(Unit.valueOf(dto.getUnit().getCode()));
        if(((Item1CDTO)dto).getParentNumber() > 0) {
            Item parent = findItemByNumber(((Item1CDTO)dto).getParentNumber())
                    .orElseThrow(() -> new BadRequestException(
                            Constants.NO_SUCH_ITEM_MESSAGE,
                            this.getClass().getName() + " - updateItemFields(Item item, ItemDTO dto)"));
            item.setParent(parent);
        }
    }

}
