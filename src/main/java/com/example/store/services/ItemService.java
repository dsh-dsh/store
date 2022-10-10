package com.example.store.services;

import com.example.store.components.TreeBuilder;
import com.example.store.exceptions.BadRequestException;
import com.example.store.mappers.ItemMapper;
import com.example.store.model.dto.ItemDTO;
import com.example.store.model.dto.ItemDTOForDir;
import com.example.store.model.dto.ItemDTOForList;
import com.example.store.model.dto.ItemDTOForTree;
import com.example.store.model.entities.Item;
import com.example.store.model.entities.PropertySetting;
import com.example.store.model.enums.Unit;
import com.example.store.model.enums.Workshop;
import com.example.store.repositories.ItemRepository;
import com.example.store.utils.Constants;
import com.example.store.utils.Util;
import com.example.store.utils.annotations.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemService {

    @Autowired
    protected ItemRepository itemRepository;
    @Autowired
    protected ItemRestService itemRestService;
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
    @Autowired
    @Qualifier("ingredientDir")
    private PropertySetting ingredientDirSetting;

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

    public void updateItem(ItemDTO itemDTO, long longDate) {
        LocalDate date = Util.getLocalDate(longDate);
        Item item = findItemById(itemDTO.getId());
        updateItemFields(item, itemDTO);
        itemRepository.save(item);
        priceService.updateItemPrices(item, itemDTO.getPrices(), date);
        setService.updateSets(item, itemDTO.getSets());
        ingredientService.updateIngredients(item, itemDTO.getIngredients());
        // todo в тесте не добавляется quantities в третьем ингредиенте
    }

    public List<ItemDTOForList> getItemDTOList(long time) {
        LocalDateTime dateTime = time != 0 ? Util.getLocalDateTime(time) : null;
        List<Item> items = getIngredientItemsList(itemRepository.findByParentIds(List.of(ingredientDirSetting.getProperty())));
        return items.stream()
                .sorted(Comparator.comparing(Item::getName))
                .map(item -> mapToDTOForList(item, dateTime))
                .collect(Collectors.toList());
    }

    public List<Item> getIngredientItemsList(List<Item> items) {
        List<Item> itemList = new ArrayList<>();
        for(Item item : items) {
            if(item.isNode()) {
                itemList.addAll(itemRepository.findByParentIds(List.of(item.getId())));
            } else {
                itemList.add(item);
            }
        }
        return itemList;
    }

    public List<ItemDTOForDir> getItemDirList() {
        List<Item> items = itemRepository.getByIsNode(true);
        return items.stream()
                .map(itemMapper::mapToDTOForDir)
                .collect(Collectors.toList());
    }

    protected ItemDTOForList mapToDTOForList(Item item, LocalDateTime dateTime) {
        ItemDTOForList dto = new ItemDTOForList();
        dto.setId(item.getId());
        dto.setName(item.getName());
        if(dateTime != null) {
            dto.setRestList(itemRestService.getItemRestList(item, dateTime));
            dto.setPrice(itemRestService.getLastPriceOfItem(item, dateTime));
        }
        dto.setParentId(item.getParentId());
        return dto;
    }

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
        item.setParent(findItemById(dto.getParentId()));
    }

    public Item findItemById(int id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(Constants.NO_SUCH_ITEM_MESSAGE));
    }

    public Item getItemById(int id) {
        return itemRepository.getById(id);
    }

    public Item getItemByNumber(int number) {
        return itemRepository.findByNumber(number)
                .orElseThrow(() -> new BadRequestException(String.format(Constants.NO_SUCH_INGREDIENT_MESSAGE, number)));
    }

    public Optional<Item> findItemByNumber(int number) {
        return itemRepository.findByNumber(number);
    }

    public ItemDTO getItemDTOById(int id, long longDate) {
        LocalDate date = Util.getLocalDate(longDate);
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

    public Item getParent(Item child) {
        int parentId = itemRepository.getParentId(child.getId());
        return itemRepository.findById(parentId)
                .orElseThrow(() -> new BadRequestException(Constants.NO_SUCH_ITEM_MESSAGE));
    }
}
