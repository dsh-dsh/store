package com.example.store.services;

import com.example.store.exceptions.BadRequestException;
import com.example.store.mappers.ItemMapper;
import com.example.store.model.dto.ItemDTO;
import com.example.store.model.entities.Item;
import com.example.store.repositories.ItemRepository;
import com.example.store.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        itemDTO.getPrices()
                .forEach(priceDTO -> priceService.setNewPrice(priceDTO, item));
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
