package com.example.sklad.mappers;

import com.example.sklad.model.dto.CheckInfoDTO;
import com.example.sklad.model.dto.DocItemDTO;
import com.example.sklad.model.dto.documents.DocDTO;
import com.example.sklad.model.dto.documents.OrderDTO;
import com.example.sklad.model.entities.CheckInfo;
import com.example.sklad.model.entities.DocumentItem;
import com.example.sklad.model.entities.documents.ItemDoc;
import com.example.sklad.model.entities.documents.OrderDoc;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DocMapper extends MappingConverters {

    private final ModelMapper modelMapper;

    protected final Converter<List<DocumentItem>, List<DocItemDTO>> docItemsConverter =
            items -> {
                    return List.of(new DocItemDTO());
            };

    protected final Converter<CheckInfo, CheckInfoDTO> checkInfoConverter =
            items -> {
                return new CheckInfoDTO();
            };

    public DocMapper() {
        this.modelMapper = new ModelMapper();
        modelMapper.createTypeMap(ItemDoc.class, DocDTO.class)
                .addMappings(mapper -> mapper.using(dateTimeConverter).map(ItemDoc::getDateTime, DocDTO::setTime))
                .addMappings(mapper -> mapper.using(docItemsConverter).map(ItemDoc::getDocumentItems, DocDTO::setDocItems))
                .addMappings(mapper -> mapper.using(checkInfoConverter).map(ItemDoc::getThis, DocDTO::setCheckInfo));
    }

    public DocDTO mapToDocDTO(ItemDoc itemDoc) {
        return modelMapper.map(itemDoc, DocDTO.class);
    }

}
