package com.example.store.mappers;

import com.example.store.model.dto.Item1CDTO;
import com.example.store.model.dto.ItemDTO;
import com.example.store.model.dto.ItemDTOForList;
import com.example.store.model.dto.ItemDTOForTree;
import com.example.store.model.entities.Item;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@Component
public class ItemMapper extends MappingConverters {

    private final ModelMapper modelMapper;

    @PostConstruct
    public void init() {
        this.modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        this.modelMapper.createTypeMap(Item.class, ItemDTO.class)
                .addMappings(mapper -> mapper.using(dateTimeToLongConverter).map(Item::getRegTime, ItemDTO::setRegTime))
                .addMappings(mapper -> mapper.using(workshopConverter).map(Item::getWorkshop, ItemDTO::setWorkshop))
                .addMappings(mapper -> mapper.using(unitConverter).map(Item::getUnit, ItemDTO::setUnit))
                .addMappings(mapper -> mapper.using(parentConverter).map(Item::getParent, ItemDTO::setParentId));
        this.modelMapper.createTypeMap(ItemDTO.class, Item.class)
                .addMappings(mapper -> mapper.using(longToDateTimeConverter).map(ItemDTO::getRegTime, Item::setRegTime))
                .addMappings(mapper -> mapper.using(workshopDTOConverter).map(ItemDTO::getWorkshop, Item::setWorkshop))
                .addMappings(mapper -> mapper.using(unitDTOConverter).map(ItemDTO::getUnit, Item::setUnit))
                .addMappings(mapper -> mapper.skip(ItemDTO::getParentId, Item::setParent));
        this.modelMapper.createTypeMap(Item1CDTO.class, Item.class)
                .addMappings(mapper -> mapper.using(longToDateTimeConverter).map(Item1CDTO::getRegTime, Item::setRegTime))
                .addMappings(mapper -> mapper.using(workshopDTOConverter).map(Item1CDTO::getWorkshop, Item::setWorkshop))
                .addMappings(mapper -> mapper.using(unitDTOConverter).map(Item1CDTO::getUnit, Item::setUnit))
                .addMappings(mapper -> mapper.skip(Item1CDTO::getParentId, Item::setParent));
        this.modelMapper.createTypeMap(Item.class, ItemDTOForTree.class)
                .addMappings(mapper -> mapper.map(Item::getId, ItemDTOForTree::setData))
                .addMappings(mapper -> mapper.map(Item::getName, ItemDTOForTree::setLabel))
                .addMappings(mapper -> mapper.using(parentConverter).map(Item::getParent, ItemDTOForTree::setParentId));
        this.modelMapper.createTypeMap(Item.class, ItemDTOForList.class)
                .addMappings(mapper -> mapper.using(restConverter).map(src -> src, ItemDTOForList::setRestList))
                .addMappings(mapper -> mapper.using(parentConverter).map(Item::getParent, ItemDTOForList::setParentId));
    }

    public ItemDTO mapToDTO(Item item) {
        return modelMapper.map(item, ItemDTO.class);
    }
    public Item mapToItem(ItemDTO dto) {
        return modelMapper.map(dto, Item.class);
    }
    public ItemDTOForTree mapToDTOForTree(Item item) {
        return modelMapper.map(item, ItemDTOForTree.class);
    }
    public ItemDTOForList mapToDTOForList(Item item) {
        return modelMapper.map(item, ItemDTOForList.class);
    }
}
