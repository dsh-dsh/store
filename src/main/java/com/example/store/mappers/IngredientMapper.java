package com.example.store.mappers;

import com.example.store.model.dto.IngredientDTO;
import com.example.store.model.dto.ItemDTO;
import com.example.store.model.dto.QuantityDTO;
import com.example.store.model.entities.Ingredient;
import com.example.store.model.entities.Item;
import com.example.store.model.entities.Quantity;
import com.example.store.services.ItemService;
import com.example.store.services.QuantityService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@RequiredArgsConstructor
@Component
public class IngredientMapper {

    private final ModelMapper modelMapper;
    private final QuantityService quantityService;
    private final ItemService itemService;

    private final Converter<Item, ItemDTO> itemConverter = item -> getItemDTO(item.getSource());
    private final Converter<ItemDTO, Item> itemDTOConverter = dto -> getItem(dto.getSource());
//    private final Converter<List<Quantity>, List<QuantityDTO>> quantityConverter =

    @PostConstruct
    private void init() {
        modelMapper.createTypeMap(Ingredient.class, IngredientDTO.class)
                .addMappings(mapper -> mapper.using(itemConverter).map(Ingredient::getParent, IngredientDTO::setParent))
                .addMappings(mapper -> mapper.using(itemConverter).map(Ingredient::getChild, IngredientDTO::setChild));
        modelMapper.createTypeMap(IngredientDTO.class, Ingredient.class)
                .addMappings(mapper -> mapper.using(itemDTOConverter).map(IngredientDTO::getParent, Ingredient::setParent))
                .addMappings(mapper -> mapper.using(itemDTOConverter).map(IngredientDTO::getChild, Ingredient::setChild))
                .addMappings(mapper -> mapper.skip(IngredientDTO::getQuantityList, Ingredient::setQuantityList));
    }

    public IngredientDTO mapToDTO(Ingredient ingredient) {
        return modelMapper.map(ingredient, IngredientDTO.class);
    }

    public Ingredient mapToEntity(IngredientDTO ingredientDTO) {
        return modelMapper.map(ingredientDTO, Ingredient.class);
    }

    private ItemDTO getItemDTO(Item item) {
        ItemDTO dto = new ItemDTO();
        dto.setId(item.getId());
        dto.setName(item.getName());
        return dto;
    }

    private Item getItem(ItemDTO dto) {
        if(dto == null) return null;
        return itemService.getItemById(dto.getId());
    }

}