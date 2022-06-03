package com.example.store.services;

import com.example.store.components.TreeBuilder;
import com.example.store.exceptions.BadRequestException;
import com.example.store.mappers.ItemMapper;
import com.example.store.model.dto.ItemDTO;
import com.example.store.model.dto.ItemDTOForList;
import com.example.store.model.dto.ItemDTOForTree;
import com.example.store.model.entities.Item;
import com.example.store.model.enums.Unit;
import com.example.store.model.enums.Workshop;
import com.example.store.repositories.ItemRepository;
import com.example.store.utils.Constants;
import com.example.store.utils.annotations.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemService {

    @Autowired
    protected ItemRepository itemRepository;
    @Autowired
    protected ItemMapper itemMapper;
    @Autowired
    protected PriceService priceService;
    @Autowired
    protected SetService setService;
    @Autowired
    protected IngredientService ingredientService;
    @Autowired
    private TreeBuilder<Item> treeBuilder;

    public List<ItemDTOForTree> getItemDTOTree() {
        List<Item> items = itemRepository.findAll(Sort.by("id"));
        return treeBuilder.getItemTree(items);
    }

    public Item setNewItem(ItemDTO itemDTO) {
        Item item = itemMapper.mapToItem(itemDTO);
        item.setParent(findItemById(itemDTO.getParentId()));
        itemRepository.save(item);
        priceService.addPrices(item, itemDTO);
        setService.setSets(item, itemDTO.getSets());
        ingredientService.setIngredients(item, itemDTO.getIngredients());
        return item;
    }

    // TODO add tests

    public void updateItem(ItemDTO itemDTO, String stringDate) {
        LocalDate date = LocalDate.parse(stringDate);
        Item item = findItemById(itemDTO.getId());
        updateItemFields(item, itemDTO);
        itemRepository.save(item);
        priceService.updateItemPrices(item, itemDTO.getPrices(), date);
        setService.updateSets(item, itemDTO.getSets());
        ingredientService.updateIngredients(item, itemDTO.getIngredients());
    }

    public List<ItemDTOForList> getItemDTOList() {
        List<Integer> parentIds = List.of(3);
        List<Item> items = itemRepository.findByParentIds(parentIds);
        return items.stream().map(itemMapper::mapToDTOForList).collect(Collectors.toList());
    }

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
        item.setParent(findItemById(dto.getParentId()));
    }

    public Item getItemById(int id) {
        return itemRepository.getById(id);
    }

    public Optional<Item> findItemByNumber(int number) {
        return itemRepository.findByNumber(number);
    }

    public int getItemIdByNumber(int number) {
        return itemRepository.getItemIdByNumber(number);
    }

    public ItemDTO getItemDTOById(int id, String stringDate) {
        LocalDate date = LocalDate.parse(stringDate);
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(Constants.NO_SUCH_ITEM_MESSAGE));
        item.setParent(getParent(item));
        item.setPrices(priceService.getPriceListOfItem(item, date));
        ItemDTO itemDTO = itemMapper.mapToDTO(item);
        itemDTO.setSets(setService.getSets(item));
        itemDTO.setIngredients(ingredientService.getIngredientDTOList(item, date));

        return itemDTO;
    }

    @Transaction
    public void softDeleteItem(int id) {
        Item item = findItemById(id);
        item.setDeleted(true);
        itemRepository.save(item);
        ingredientService.softDeleteIngredients(item, LocalDate.now());
    }

    public Item findItemById(int id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(Constants.NO_SUCH_ITEM_MESSAGE));
    }

    public Item getParent(Item child) {
        int parentId = itemRepository.getParentId(child.getId());
        return itemRepository.findById(parentId)
                .orElseThrow(() -> new BadRequestException(Constants.NO_SUCH_ITEM_MESSAGE));
    }
}
