package com.example.sklad.services;

import com.example.sklad.factories.CheckFactory;
import com.example.sklad.factories.OrderFactory;
import com.example.sklad.model.dto.requests.CheckRequestDTO;
import com.example.sklad.model.dto.requests.OrderRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocsFrom1cService {

    @Autowired
    private CheckFactory checkFactory;
    @Autowired
    private OrderFactory orderFactory;

    public void addCheckDocsFrom1C(CheckRequestDTO checkRequestDTO) {
        checkRequestDTO.getCheckDTOList()
                .forEach(itemDocDTO -> {
                    checkFactory.setItemDocDTO(itemDocDTO);
                    checkFactory.createDocumentFrom1C();
                });
    }

    public void addOrderDocsFrom1C(OrderRequestDTO orderRequestDTO) {
        orderRequestDTO.getOrderDTOList()
                .forEach(orderDTO -> {
                    orderFactory.setOrderDTO(orderDTO);
                    orderFactory.createDocumentFrom1C();
                });
    }

}
