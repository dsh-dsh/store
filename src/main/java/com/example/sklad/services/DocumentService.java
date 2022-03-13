package com.example.sklad.services;

import com.example.sklad.factories.CheckFactory;
import com.example.sklad.factories.ReceiptDocFactory;
import com.example.sklad.model.dto.documents.ItemDocDTO;
import com.example.sklad.model.responses.ListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentService {

    @Autowired
    private CheckFactory checkFactory;
    @Autowired
    private ReceiptDocFactory receiptDocFactory;

    public void addCheckDoc(ItemDocDTO itemDocDTO) {
        checkFactory.setItemDocDTO(itemDocDTO);
        checkFactory.createDocument();
    }

    public void addReceiptDoc(ItemDocDTO itemDocDTO) {
        receiptDocFactory.setItemDocDTO(itemDocDTO);
        receiptDocFactory.createDocument();
    }

    public ListResponse<ItemDocDTO> getDocuments() {
        return new ListResponse<>(List.of(new ItemDocDTO()), null);

    }
}
