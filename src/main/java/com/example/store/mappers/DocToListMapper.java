package com.example.store.mappers;

import com.example.store.model.dto.documents.DocToListDTO;
import com.example.store.model.entities.documents.Document;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

@Component
public class DocToListMapper extends MappingConverters {

    private final ModelMapper modelMapper;

    public DocToListMapper() {
        this.modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.createTypeMap(Document.class, DocToListDTO.class)
                .addMappings(mapper -> mapper.using(dateTimeToLongConverter).map(Document::getDateTime, DocToListDTO::setDateTime))
                .addMappings(mapper -> mapper.using(docTypeConverter).map(Document::getDocType, DocToListDTO::setDocType));
    }

    public DocToListDTO mapToDocDTO(Document document) {
        return modelMapper.map(document, DocToListDTO.class);
    }

}
