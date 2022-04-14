package com.example.store.services;

import com.example.store.factories.docs1s.Doc1cFactory;
import com.example.store.factories.docs1s.Order1cFactory;
import com.example.store.model.dto.requests.ItemDocListRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocsFrom1cService {

    @Autowired
    private Doc1cFactory doc1CFactory;
    @Autowired
    private Order1cFactory order1cFactory;

    public void addDocsFrom1C(ItemDocListRequestDTO itemDocListRequestDTO) {
        itemDocListRequestDTO.getCheckDTOList()
                .forEach(doc1CFactory::addDocument);
    }

}
