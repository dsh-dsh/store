package com.example.store.services;

import com.example.store.exceptions.BadRequestException;
import com.example.store.factories.orderdoc.CreditOrderFactory;
import com.example.store.factories.orderdoc.WithdrawOrderFactory;
import com.example.store.mappers.OrderMapper;
import com.example.store.model.dto.documents.DocDTO;
import com.example.store.model.dto.documents.OrderDTO;
import com.example.store.model.entities.documents.OrderDoc;
import com.example.store.model.enums.DocumentType;
import com.example.store.repositories.OrderDocRepository;
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
    @Autowired
    private OrderMapper orderMapper;

    public OrderDTO getOrderById(int id) {
        OrderDoc order = orderDocRepository.findById(id)
                .orElseThrow(BadRequestException::new);
        return orderMapper.mapToOrderDTO(order);
    }

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

    public void deleteOrderDoc(DocDTO docDTO) {
        int docId = docDTO.getId();
        withdrawFactory.deleteDocument(docId);
    }


    public List<OrderDoc> getDocumentsByType(DocumentType documentType) {
        return orderDocRepository.findByDocType(documentType);
    }
}
