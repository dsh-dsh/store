package com.example.sklad.mappers;

import com.example.sklad.model.dto.DocItemDTO;
import com.example.sklad.model.entities.DocumentItem;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class DocItemMapper {

    private final ModelMapper modelMapper;

    public DocItemMapper() {
        this.modelMapper = new ModelMapper();
    }

    public DocItemDTO mapToDocItemDTO(DocumentItem item) {
        return modelMapper.map(item, DocItemDTO.class);
    }
}
