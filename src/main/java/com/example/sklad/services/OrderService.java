package com.example.sklad.services;

import com.example.sklad.factories.orderdoc.CreditOrderFactory;
import com.example.sklad.factories.orderdoc.WithdrawOrderFactory;
import com.example.sklad.model.dto.documents.DocDTO;
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

    public void addRKO(DocDTO docDTO) {
        withdrawFactory.addDocument(docDTO);
    }

    public void updateRKO(DocDTO docDTO) {
        withdrawFactory.updateDocument(docDTO);
    }

    public void addPKO(DocDTO docDTO) {
        creditOrderFactory.addDocument(docDTO);
    }

    public void updatePKO(DocDTO docDTO) {
        creditOrderFactory.updateDocument(docDTO);
    }


    public List<OrderDoc> getDocumentsByType(DocumentType documentType) {
        return orderDocRepository.findByDocType(documentType);
    }
}
