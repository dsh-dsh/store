package com.example.store.mappers;

import com.example.store.model.dto.Item1CDTO;
import com.example.store.model.dto.ItemDTO;
import com.example.store.model.dto.ItemDTOForDir;
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
        this.modelMapper.createTypeMap(Item.class, ItemDTOForDir.class);
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
        this.modelMapper.createTypeMap(Item.class, Item1CDTO.class)
                .addMappings(mapper -> mapper.using(dateTimeToLongConverter).map(Item::getRegTime, Item1CDTO::setRegTime))
                .addMappings(mapper -> mapper.using(workshopConverter).map(Item::getWorkshop, Item1CDTO::setWorkshop))
                .addMappings(mapper -> mapper.using(unitConverter).map(Item::getUnit, Item1CDTO::setUnit))
                .addMappings(mapper -> mapper.using(parentNumberConverter).map(Item::getParent, Item1CDTO::setParentNumber));
    }

    public ItemDTO mapToDTO(Item item) {
        return modelMapper.map(item, ItemDTO.class);
    }

    public ItemDTOForDir mapToDTOForDir(Item item) {
        return modelMapper.map(item, ItemDTOForDir.class);
    }

    public Item mapToItem(ItemDTO dto) {
        return modelMapper.map(dto, Item.class);
    }

    public Item1CDTO mapTo1CDTO(Item item) {
        return modelMapper.map(item, Item1CDTO.class);
    }
}
