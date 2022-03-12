package com.example.sklad.services;

import com.example.sklad.factories.Doc1cFactory;
import com.example.sklad.factories.Order1cFactory;
import com.example.sklad.model.dto.requests.ItemDocRequestDTO;
import com.example.sklad.model.dto.requests.OrderRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocsFrom1cService {

    @Autowired
    private Doc1cFactory doc1CFactory;
    @Autowired
    private Order1cFactory order1cFactory;

    public void addCheckDocsFrom1C(ItemDocRequestDTO itemDocRequestDTO) {
        itemDocRequestDTO.getCheckDTOList()
                .forEach(itemDocDTO -> {
                    doc1CFactory.setItemDocDTO(itemDocDTO);
                    doc1CFactory.createDocument();
                });
    }

    public void addOrderDocsFrom1C(OrderRequestDTO orderRequestDTO) {
        orderRequestDTO.getOrderDTOList()
                .forEach(orderDTO -> {
                    order1cFactory.setOrderDTO(orderDTO);
                    order1cFactory.createDocument();
                });
    }

}
