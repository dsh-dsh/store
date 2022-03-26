package com.example.store.services;

import com.example.store.factories.docs1s.Doc1cFactory;
import com.example.store.factories.docs1s.Order1cFactory;
import com.example.store.model.dto.requests.ItemDocListRequestDTO;
import com.example.store.model.dto.requests.OrderRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocsFrom1cService {

    @Autowired
    private Doc1cFactory doc1CFactory;
    @Autowired
    private Order1cFactory order1cFactory;

    public void addCheckDocsFrom1C(ItemDocListRequestDTO itemDocListRequestDTO) {
        itemDocListRequestDTO.getCheckDTOList()
                .forEach(doc1CFactory::addDocument);
    }

    public void addOrderDocsFrom1C(OrderRequestDTO orderRequestDTO) {
        orderRequestDTO.getDocDTO()
                .forEach(order1cFactory::addDocument);
    }

}
