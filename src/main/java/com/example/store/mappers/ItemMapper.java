package com.example.store.mappers;

import com.example.store.model.dto.ItemDTO;
import com.example.store.model.entities.Item;
import com.example.store.model.enums.Unit;
import com.example.store.model.enums.Workshop;
import lombok.RequiredArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@Component
public class ItemMapper extends MappingConverters {

    private final ModelMapper modelMapper;

    private final Converter<Workshop, String> workshopConverter = shop -> shop.getSource().toString();
    private final Converter<String, Workshop> stringWorkshopConverter = str -> Workshop.valueOf(str.getSource());
    private final Converter<Unit, String> unitConverter = unit -> unit.getSource().toString();
    private final Converter<String, Unit> stringUnitConverter = str -> Unit.valueOf(str.getSource());
    private final Converter<Item, Integer> parentConverter = item -> item.getSource().getId();

    @PostConstruct
    public void init() {
        this.modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        this.modelMapper.createTypeMap(Item.class, ItemDTO.class)
//                .addMappings(mapper -> mapper.using(dateTimeConverter).map(Item::getRegTime, ItemDTO::setRegTime))
                .addMappings(mapper -> mapper.using(workshopConverter).map(Item::getWorkshop, ItemDTO::setWorkshop))
                .addMappings(mapper -> mapper.using(unitConverter).map(Item::getUnit, ItemDTO::setUnit))
                .addMappings(mapper -> mapper.using(parentConverter).map(Item::getParent, ItemDTO::setParentId));
        this.modelMapper.createTypeMap(ItemDTO.class, Item.class)
                .addMappings(mapper -> mapper.using(stringToDateTime).map(ItemDTO::getRegTime, Item::setRegTime))
                .addMappings(mapper -> mapper.using(stringWorkshopConverter).map(ItemDTO::getWorkshop, Item::setWorkshop))
                .addMappings(mapper -> mapper.using(stringUnitConverter).map(ItemDTO::getUnit, Item::setUnit))
                .addMappings(mapper -> mapper.skip(ItemDTO::getParentId, Item::setParent));
    }

    public ItemDTO mapToDTO(Item item) {
        return modelMapper.map(item, ItemDTO.class);
    }

    public Item mapToItem(ItemDTO dto) {
        return modelMapper.map(dto, Item.class);
    }
}
