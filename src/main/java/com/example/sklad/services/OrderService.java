package com.example.sklad.services;

import com.example.sklad.factories.orderdoc.CreditOrderFactory;
import com.example.sklad.factories.orderdoc.WithdrawOrderFactory;
import com.example.sklad.model.dto.documents.ItemDocDTO;
import com.example.sklad.model.entities.documents.OrderDoc;
import com.example.sklad.model.enums.DocumentType;
import com.example.sklad.repositories.OrderDocRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private WithdrawOrderFactory withdrawFactory;
    @Autowired
    private CreditOrderFactory creditOrderFactory;
    @Autowired
    private OrderDocRepository orderDocRepository;

    public void addRKO(ItemDocDTO docDTO) {
        withdrawFactory.addDocument(docDTO);
    }

    public void updateRKO(ItemDocDTO docDTO) {
        withdrawFactory.updateDocument(docDTO);
    }

    public void addPKO(ItemDocDTO docDTO) {
        creditOrderFactory.addDocument(docDTO);
    }

    public void updatePKO(ItemDocDTO docDTO) {
        creditOrderFactory.updateDocument(docDTO);
    }


    public List<OrderDoc> getDocumentsByType(DocumentType documentType) {
        return orderDocRepository.findByDocType(documentType);
    }
}
