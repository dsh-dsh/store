package com.example.sklad.services;

import com.example.sklad.factories.CheckFactory;
import com.example.sklad.model.dto.documents.ItemDocDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocumentService {

    @Autowired
    private CheckFactory factory;

    public void addCheckDoc(ItemDocDTO itemDocDTO) {
        factory.setItemDocDTO(itemDocDTO);
        factory.createDocument();
    }
}
