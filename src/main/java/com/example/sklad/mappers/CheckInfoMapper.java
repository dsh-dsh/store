package com.example.sklad.mappers;

import com.example.sklad.model.dto.CheckInfoDTO;
import com.example.sklad.model.entities.CheckInfo;
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
