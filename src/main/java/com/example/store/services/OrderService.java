package com.example.store.services;

import com.example.store.exceptions.BadRequestException;
import com.example.store.factories.orderdoc.OrderDocFactory;
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
    private OrderDocFactory orderDocFactory;
    @Autowired
    private OrderDocRepository orderDocRepository;
    @Autowired
    private OrderMapper orderMapper;

    public OrderDTO getOrderById(int id) {
        OrderDoc order = orderDocRepository.findById(id)
                .orElseThrow(BadRequestException::new);
        return orderMapper.mapToOrderDTO(order);
    }

    public void addOrder(DocDTO docDTO) {
        orderDocFactory.addDocument(docDTO);
    }

    public void updateOrder(DocDTO docDTO) {
        orderDocFactory.updateDocument(docDTO);
    }

    public void deleteOrderDoc(DocDTO docDTO) {
        int docId = docDTO.getId();
        orderDocFactory.deleteDocument(docId);
    }

    public List<OrderDoc> getDocumentsByType(DocumentType documentType) {
        return orderDocRepository.findByDocType(documentType);
    }
}
