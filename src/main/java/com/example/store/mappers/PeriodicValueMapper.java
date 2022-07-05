package com.example.store.mappers;

import com.example.store.model.dto.PeriodicValueDTO;
import com.example.store.model.entities.PeriodicValue;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@Component
public class PeriodicValueMapper extends MappingConverters{

    private final ModelMapper modelMapper;

    @PostConstruct
    private void init() {
        modelMapper.createTypeMap(PeriodicValue.class, PeriodicValueDTO.class)
                .addMappings(mapper -> mapper.using(dateToLongConverter).map(PeriodicValue::getDate, PeriodicValueDTO::setDate));
        modelMapper.createTypeMap(PeriodicValueDTO.class, PeriodicValue.class)
                .addMappings(mapper -> mapper.using(longToDateConverter).map(PeriodicValueDTO::getDate, PeriodicValue::setDate))
                .addMappings(mapper -> mapper.using(typeConverter).map(PeriodicValueDTO::getType, PeriodicValue::setType));
    }

    public PeriodicValueDTO mapToDTO(PeriodicValue periodicValue) {
        return modelMapper.map(periodicValue, PeriodicValueDTO.class);
    }

    public PeriodicValue mapToItem(PeriodicValueDTO dto) {
        return modelMapper.map(dto, PeriodicValue.class);
    }
}
