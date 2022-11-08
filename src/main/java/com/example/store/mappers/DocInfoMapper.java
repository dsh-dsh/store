package com.example.store.mappers;

import com.example.store.model.dto.DocInfoDTO;
import com.example.store.model.entities.DocInfo;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

@Component
public class DocInfoMapper {

    private final ModelMapper modelMapper;

    public DocInfoMapper() {
        this.modelMapper = new ModelMapper();
        this.modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        this.modelMapper.createTypeMap(DocInfo.class, DocInfoDTO.class);
        this.modelMapper.createTypeMap(DocInfoDTO.class, DocInfo.class);
    }

    public DocInfoDTO mapToDTO(DocInfo docInfo) {
        return modelMapper.map(docInfo, DocInfoDTO.class);
    }
    public DocInfo mapToDocInfo(DocInfoDTO docInfoDTO) {
        return modelMapper.map(docInfoDTO, DocInfo.class);
    }
}
