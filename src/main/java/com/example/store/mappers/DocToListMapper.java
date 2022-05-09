package com.example.store.mappers;

import com.example.store.model.dto.documents.DocToListDTO;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.enums.DocumentType;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

@Component
public class DocToListMapper extends MappingConverters {

    private final ModelMapper modelMapper;

    private static final Converter<DocumentType, String> typeConverter = type -> type.getSource().getValue();

    public DocToListMapper() {
        this.modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.createTypeMap(Document.class, DocToListDTO.class)
                .addMappings(mapper -> mapper.using(dateTimeConverter).map(Document::getDateTime, DocToListDTO::setTime))
                .addMappings(mapper -> mapper.using(typeConverter).map(Document::getDocType, DocToListDTO::setType));
//                .addMappings(mapper -> mapper.skip(Document::));
    }

    public DocToListDTO mapToDocDTO(Document document) {
        return modelMapper.map(document, DocToListDTO.class);
    }

}
