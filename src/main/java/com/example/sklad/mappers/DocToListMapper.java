package com.example.sklad.mappers;

import com.example.sklad.model.dto.documents.DocToListDTO;
import com.example.sklad.model.entities.documents.Document;
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
                .addMappings(mapper -> mapper.using(dateTimeConverter).map(Document::getDateTime, DocToListDTO::setTime));
//                .addMappings(mapper -> mapper.skip(Document::getDateTime, DocToListDTO::setTime));
    }

    public DocToListDTO mapToDocDTO(Document document) {
        return modelMapper.map(document, DocToListDTO.class);
    }

}
