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
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@RequiredArgsConstructor
@Component
public class IngredientMapper {

    @Autowired
    private QuantityService quantityService;

    private final ModelMapper modelMapper;
    private final ItemService itemService;

    private final Converter<Item, ItemDTO> itemConverter = item -> getItemDTO(item.getSource());
    private final Converter<ItemDTO, Item> itemDTOConverter = dto -> getItem(dto.getSource());
//    private final Converter<List<Quantity>, List<QuantityDTO>> quantityConverter =
//            list -> quantityService.getQuantityDTOList(list.getSource());
//    private final Converter<List<QuantityDTO>, List<Quantity>> quantityDTOConverter =
//            list ->

    @PostConstruct
    private void init() {
        modelMapper.createTypeMap(Ingredient.class, IngredientDTO.class)
                .addMappings(mapper -> mapper.using(itemConverter).map(Ingredient::getParent, IngredientDTO::setParent))
                .addMappings(mapper -> mapper.using(itemConverter).map(Ingredient::getChild, IngredientDTO::setChild))
                .addMappings(mapper -> mapper.skip(Ingredient::getQuantityList, IngredientDTO::setQuantityList));
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
