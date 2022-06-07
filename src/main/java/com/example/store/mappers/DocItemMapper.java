package com.example.store.mappers;

import com.example.store.model.dto.DocItemDTO;
import com.example.store.model.entities.DocumentItem;
import com.example.store.model.entities.Item;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MatchingStrategy;
import org.springframework.stereotype.Component;

@Component
public class DocItemMapper {

    private final ModelMapper modelMapper;

    private static final Converter<Item, String> nameConverter =
            item -> item.getSource().getName();

    private static final Converter<Item, Integer> idConverter =
            item -> item.getSource().getId();

    public DocItemMapper() {
        this.modelMapper = new ModelMapper();
        this.modelMapper.createTypeMap(DocumentItem.class, DocItemDTO.class)
                .addMappings(mapper -> mapper.using(idConverter).map(DocumentItem::getItem, DocItemDTO::setItemId))
                .addMappings(mapper -> mapper.using(nameConverter).map(DocumentItem::getItem, DocItemDTO::setItemName));
    }

    public DocItemDTO mapToDocItemDTO(DocumentItem item) {
        return modelMapper.map(item, DocItemDTO.class);
    }
}
