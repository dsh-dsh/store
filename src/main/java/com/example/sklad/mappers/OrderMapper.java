package com.example.sklad.mappers;

import com.example.sklad.model.dto.documents.OrderDTO;
import com.example.sklad.model.entities.documents.OrderDoc;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper extends Converters {

    private final ModelMapper modelMapper;

    public OrderMapper() {
        this.modelMapper = new ModelMapper();
        modelMapper.createTypeMap(OrderDoc.class, OrderDTO.class)
                .addMappings(mapper -> mapper.using(dateTimeConverter).map(OrderDoc::getDateTime, OrderDTO::setTime))
                .addMappings(mapper -> mapper.using(documentTypeStringConverter).map(OrderDoc::getDocType, OrderDTO::setDocType))
                .addMappings(mapper -> mapper.using(paymentTypeConverter).map(OrderDoc::getPaymentType, OrderDTO::setPaymentType));
    }

    public OrderDTO mapToOrderDTO(OrderDoc order) {
        return modelMapper.map(order, OrderDTO.class);
    }

}
