package com.example.sklad.services;

import com.example.sklad.exceptions.BadRequestException;
import com.example.sklad.mappers.ItemMapper;
import com.example.sklad.model.dto.ItemDTO;
import com.example.sklad.model.entities.Item;
import com.example.sklad.model.entities.Price;
import com.example.sklad.repositories.ItemRepository;
import com.example.sklad.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private PriceService priceService;


    public int setNewItem(ItemDTO itemDTO) {
        Item item = itemMapper.mapToItem(itemDTO);
        item.setParent(getParentById(itemDTO.getParentId()));
        itemRepository.save(item);
        List<Price> prices = itemDTO.getPrices().stream()
                .map(priceDTO -> priceService.setNewPrice(priceDTO, item))
                .collect(Collectors.toList());
        return item.getId();
    }

    public Item getParentById(int id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(Constants.NO_SUCH_ITEM_MESSAGE));
    }

    public Item getItemByName(String name) {
        Item item = itemRepository.findByName(name)
                .orElseThrow(() -> new BadRequestException(Constants.NO_SUCH_ITEM_MESSAGE));
        item.setPrices(priceService.getPriceListOfItem(item));
        return item;
    }


    public Item getItemById(int id) {
        return itemRepository.getById(id);
    }

    public Item findSetById(int id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(Constants.NO_SUCH_ITEM_MESSAGE));
    }

    public Item getParent(Item child) {
        int parentId = itemRepository.getParentId(child.getId());
        return itemRepository.findById(parentId)
                .orElseThrow(() -> new BadRequestException(Constants.NO_SUCH_ITEM_MESSAGE));
    }

//    public List<Item> getSetsIn(Item item) {
//        List<Integer> dinnerIdList = itemRepository.getDinnerIdList(item.getId());
//        return dinnerIdList.stream().map(this::findSetById).collect(Collectors.toList());
//    }

    public ItemDTO getItemDTOById(int id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(Constants.NO_SUCH_ITEM_MESSAGE));
        item.setParent(getParent(item));
        item.setPrices(priceService.getPriceListOfItem(item));
//        item.setInSets(getSetsIn(item));
        return itemMapper.mapToDTO(item);
    }

}
