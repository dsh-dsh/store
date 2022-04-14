package com.example.store.mappers;

import com.example.store.model.dto.documents.OrderDTO;
import com.example.store.model.entities.documents.OrderDoc;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper extends MappingConverters {

    private final ModelMapper modelMapper;

    public OrderMapper() {
        this.modelMapper = new ModelMapper();
        modelMapper.createTypeMap(OrderDoc.class, OrderDTO.class)
                .addMappings(mapper -> mapper.using(documentTypeStringConverter).map(OrderDoc::getDocType, OrderDTO::setDocType))
                .addMappings(mapper -> mapper.using(paymentTypeConverter).map(OrderDoc::getPaymentType, OrderDTO::setPaymentType));
    }

    public OrderDTO mapToOrderDTO(OrderDoc order) {
        return modelMapper.map(order, OrderDTO.class);
    }

}
