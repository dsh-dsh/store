package com.example.store.mappers;

import com.example.store.model.dto.documents.DocDTO;
import com.example.store.model.dto.documents.DocToListDTO;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.entities.documents.OrderDoc;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class DocMapper extends MappingConverters {

    private final ModelMapper modelMapper;

    public DocMapper() {
        this.modelMapper = new ModelMapper();

        modelMapper.createTypeMap(ItemDoc.class, DocToListDTO.class)
                .addMappings(mapper -> mapper.using(dateTimeToLongConverter).map(ItemDoc::getDateTime, DocToListDTO::setDateTime))
                .addMappings(mapper -> mapper.using(docTypeConverter).map(ItemDoc::getDocType, DocToListDTO::setDocType))
                .addMappings(mapper -> mapper.using(docItemAmountConverter).map(src -> src, DocToListDTO::setAmount));

        modelMapper.createTypeMap(OrderDoc.class, DocToListDTO.class)
                .addMappings(mapper -> mapper.using(dateTimeToLongConverter).map(OrderDoc::getDateTime, DocToListDTO::setDateTime))
                .addMappings(mapper -> mapper.using(docTypeConverter).map(OrderDoc::getDocType, DocToListDTO::setDocType));

        modelMapper.createTypeMap(OrderDoc.class, DocDTO.class)
                .addMappings(mapper -> mapper.using(dateTimeToLongConverter).map(OrderDoc::getDateTime, DocDTO::setDateTime))
                .addMappings(mapper -> mapper.using(docTypeConverter).map(OrderDoc::getDocType, DocDTO::setDocType));

        modelMapper.createTypeMap(ItemDoc.class, DocDTO.class)
                .addMappings(mapper -> mapper.using(dateTimeToLongConverter).map(ItemDoc::getDateTime, DocDTO::setDateTime))
                .addMappings(mapper -> mapper.using(docItemsConverter).map(src -> src, DocDTO::setDocItems))
                .addMappings(mapper -> mapper.using(docTypeConverter).map(ItemDoc::getDocType, DocDTO::setDocType))
                .addMappings(mapper -> mapper.when(isCheck).using(checkInfoConverter).map(src -> src, DocDTO::setCheckInfo));
    }

    public DocDTO mapToDocDTO(ItemDoc document) {
        return modelMapper.map(document, DocDTO.class);
    }
    public DocDTO mapToDocDTO(OrderDoc document) {
        return modelMapper.map(document, DocDTO.class);
    }

    public DocToListDTO mapToDocToListDTO(ItemDoc document) {
        return modelMapper.map(document, DocToListDTO.class);
    }
    public DocToListDTO mapToDocToListDTO(OrderDoc document) {
        return modelMapper.map(document, DocToListDTO.class);
    }

}
