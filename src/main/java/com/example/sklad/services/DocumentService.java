package com.example.sklad.services;

import com.example.sklad.exceptions.BadRequestException;
import com.example.sklad.factories.itemdoc.*;
import com.example.sklad.model.dto.documents.DocDTO;
import com.example.sklad.model.entities.documents.ItemDoc;
import com.example.sklad.model.enums.DocumentType;
import com.example.sklad.repositories.ItemDocRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentService {

    @Autowired
    private CheckDocFactory checkDocFactory;
    @Autowired
    private ReceiptDocFactory receiptDocFactory;
    @Autowired
    private PostingDocFactory postingDocFactory;
    @Autowired
    private RequestDocFactory requestDocFactory;
    @Autowired
    private InventoryDocFactory inventoryDocFactory;
    @Autowired
    private ItemDocRepository itemDocRepository;

    public void addCheckDoc(DocDTO docDTO) {
        checkDocFactory.addDocument(docDTO);
    }

    public void updateCheckDoc(DocDTO docDTO) {
        checkDocFactory.updateDocument(docDTO);
    }

    public void addReceiptDoc(DocDTO docDTO) {
        receiptDocFactory.addDocument(docDTO);
    }

    public void updateReceiptDoc(DocDTO docDTO) {
        receiptDocFactory.updateDocument(docDTO);
    }

    public void addPostingDoc(DocDTO docDTO) {
        postingDocFactory.addDocument(docDTO);
    }

    public void addInventoryDoc(DocDTO docDTO) {
        inventoryDocFactory.addDocument(docDTO);
    }

    public void updatePostingDoc(DocDTO docDTO) {
        postingDocFactory.updateDocument(docDTO);
    }

    public void addRequestDoc(DocDTO docDTO) {
        requestDocFactory.addDocument(docDTO);
    }

    public void updateRequestDoc(DocDTO docDTO) {
        requestDocFactory.updateDocument(docDTO);
    }

    public void updateInventoryDoc(DocDTO docDTO) {
        inventoryDocFactory.updateDocument(docDTO);
    }

    public ItemDoc getDocumentByNumber(int number) {
        return itemDocRepository.findByNumber(number)
                .orElseThrow(BadRequestException::new);
    }

    public List<ItemDoc> getDocumentsByType(DocumentType documentType) {
        return itemDocRepository.findByDocType(documentType);
    }

    public ItemDoc getDocumentById(int docId) {
        return itemDocRepository.findById(docId)
                .orElseThrow(BadRequestException::new);
    }

    public void deleteDocument(DocDTO docDTO) {
        int docId = docDTO.getId();
        inventoryDocFactory.deleteDocument(docId);
    }
}
