package com.example.store.mappers;

import com.example.store.model.dto.PriceDTO;
import com.example.store.model.entities.Price;
import lombok.RequiredArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@RequiredArgsConstructor
@Component
public class PriceMapper {

    private final ModelMapper modelMapper;

    private final Converter<LocalDate, Long> converter =
            date -> date.getSource().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();

    @PostConstruct
    private void init() {
        modelMapper.createTypeMap(Price.class, PriceDTO.class)
                .addMappings(mapper -> mapper.using(converter).map(Price::getDate, PriceDTO::setDate))
                .addMappings(mapper -> mapper.map(Price::getPriceType, PriceDTO::setType));
    }

    public PriceDTO mapToDTO(Price price) {
        return modelMapper.map(price, PriceDTO.class);
    }

}
