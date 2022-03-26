package com.example.store.mappers;

import com.example.store.model.dto.CheckInfoDTO;
import com.example.store.model.entities.CheckInfo;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

@Component
public class CheckInfoMapper {

    private final ModelMapper modelMapper;

    public CheckInfoMapper() {
        this.modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    public CheckInfoDTO mapCheckInfo(CheckInfo checkInfo) {
        return modelMapper.map(checkInfo, CheckInfoDTO.class);
    }
}
