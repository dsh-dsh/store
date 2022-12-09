package com.example.store.mappers;

import com.example.store.model.dto.DocItemDTO;
import com.example.store.model.entities.DocumentItem;
import com.example.store.model.entities.Item;
import com.example.store.utils.Util;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class DocItemMapper {

    private final ModelMapper modelMapper;

    private static final Converter<Item, String> nameConverter =
            item -> item.getSource().getName();

    private static final Converter<Item, Integer> idConverter =
            item -> item.getSource().getId();

    private static final Converter<Item, String> unitConverter =
            item -> item.getSource().getUnit().getValue();

    private static final Converter<DocumentItem, Float> amountConverter =
            item -> {
                    DocumentItem docItem = item.getSource();
                    return Util.floorValue((docItem.getQuantity().floatValue() * docItem.getPrice()) - docItem.getDiscount(), 2);
            };

    private static final Converter<DocumentItem, Float> amountFactConverter =
            item -> {
                DocumentItem docItem = item.getSource();
                return Util.floorValue(docItem.getQuantityFact() * docItem.getPrice(), 2);
            };

    public DocItemMapper() {
        this.modelMapper = new ModelMapper();
        this.modelMapper.createTypeMap(DocumentItem.class, DocItemDTO.class)
                .addMappings(mapper -> mapper.using(amountConverter).map(src -> src, DocItemDTO::setAmount))
                .addMappings(mapper -> mapper.using(amountFactConverter).map(src -> src, DocItemDTO::setAmountFact))
                .addMappings(mapper -> mapper.using(idConverter).map(DocumentItem::getItem, DocItemDTO::setItemId))
                .addMappings(mapper -> mapper.using(nameConverter).map(DocumentItem::getItem, DocItemDTO::setItemName))
                .addMappings(mapper -> mapper.using(unitConverter).map(DocumentItem::getItem, DocItemDTO::setUnit));
    }

    public DocItemDTO mapToDocItemDTO(DocumentItem item) {
        return modelMapper.map(item, DocItemDTO.class);
    }
}
