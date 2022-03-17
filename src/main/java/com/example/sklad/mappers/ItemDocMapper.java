package com.example.sklad.mappers;

import com.example.sklad.model.dto.documents.DocDTO;
import com.example.sklad.model.entities.documents.ItemDoc;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ItemDocMapper {

    private final ModelMapper modelMapper;

    public ItemDocMapper() {
        this.modelMapper = new ModelMapper();
    }

    public DocDTO mapToItemDocDTO(ItemDoc itemDoc) {
        return modelMapper.map(itemDoc, DocDTO.class);
    }
}
