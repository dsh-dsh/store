package com.example.sklad.services;

import com.example.sklad.exceptions.BadRequestException;
import com.example.sklad.factories.orderdoc.CreditOrderFactory;
import com.example.sklad.factories.orderdoc.WithdrawOrderFactory;
import com.example.sklad.mappers.OrderMapper;
import com.example.sklad.model.dto.documents.DocDTO;
import com.example.sklad.model.dto.documents.OrderDTO;
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
