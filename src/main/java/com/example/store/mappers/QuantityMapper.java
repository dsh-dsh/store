package com.example.store.mappers;

import com.example.store.model.dto.QuantityDTO;
import com.example.store.model.entities.Quantity;
import com.example.store.model.enums.QuantityType;
import lombok.RequiredArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;

@RequiredArgsConstructor
@Component
public class QuantityMapper {

    private final ModelMapper modelMapper;

    private final Converter<String, LocalDate> dateConverter = str -> LocalDate.parse(str.getSource());
    private final Converter<String, QuantityType> typeConverter = str -> QuantityType.valueOf(str.getSource());

    @PostConstruct
    private void init() {
        modelMapper.createTypeMap(Quantity.class, QuantityDTO.class);
        modelMapper.createTypeMap(QuantityDTO.class, Quantity.class)
                .addMappings(mapper -> mapper.using(dateConverter).map(QuantityDTO::getDate, Quantity::setDate))
                .addMappings(mapper -> mapper.using(typeConverter).map(QuantityDTO::getType, Quantity::setType));
    }

    public QuantityDTO mapToDTO(Quantity quantity) {
        return modelMapper.map(quantity, QuantityDTO.class);
    }

    public Quantity mapToItem(QuantityDTO dto) {
        return modelMapper.map(dto, Quantity.class);
    }
}
