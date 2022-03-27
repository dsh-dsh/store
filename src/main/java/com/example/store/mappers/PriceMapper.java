package com.example.store.mappers;

import com.example.store.model.dto.PriceDTO;
import com.example.store.model.entities.Price;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@Component
public class PriceMapper {

    private final ModelMapper modelMapper;

    @PostConstruct
    private void init() {
        modelMapper.createTypeMap(Price.class, PriceDTO.class)
                .addMappings(mapper -> mapper.map(Price::getPriceType, PriceDTO::setType));
    }

    public PriceDTO mapToDTO(Price price) {
        return modelMapper.map(price, PriceDTO.class);
    }

}
