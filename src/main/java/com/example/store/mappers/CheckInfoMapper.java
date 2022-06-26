package com.example.store.mappers;

import com.example.store.model.dto.CheckInfoDTO;
import com.example.store.model.entities.CheckInfo;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

@Component
public class CheckInfoMapper extends MappingConverters {

    private final ModelMapper modelMapper;

    public CheckInfoMapper() {
        this.modelMapper = new ModelMapper();
        this.modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        this.modelMapper.createTypeMap(CheckInfo.class, CheckInfoDTO.class)
                .addMappings(mapper -> mapper.using(dateTimeToLongConverter).map(CheckInfo::getDateTime, CheckInfoDTO::setDateTime));
    }

    public CheckInfoDTO mapCheckInfo(CheckInfo checkInfo) {
        return modelMapper.map(checkInfo, CheckInfoDTO.class);
    }
}
