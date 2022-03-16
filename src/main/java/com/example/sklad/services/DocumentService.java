package com.example.sklad.services;

import com.example.sklad.exceptions.BadRequestException;
import com.example.sklad.factories.itemdoc.CheckDocFactory;
import com.example.sklad.factories.itemdoc.PostingDocFactory;
import com.example.sklad.factories.itemdoc.ReceiptDocFactory;
import com.example.sklad.factories.itemdoc.RequestDocFactory;
import com.example.sklad.model.dto.documents.ItemDocDTO;
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
    private ItemDocRepository itemDocRepository;

    public void addCheckDoc(ItemDocDTO itemDocDTO) {
        checkDocFactory.addDocument(itemDocDTO);
    }

    public void updateCheckDoc(ItemDocDTO itemDocDTO) {
        checkDocFactory.updateDocument(itemDocDTO);
    }

    public void addReceiptDoc(ItemDocDTO itemDocDTO) {
        receiptDocFactory.addDocument(itemDocDTO);
    }

    public void updateReceiptDoc(ItemDocDTO itemDocDTO) {
        receiptDocFactory.updateDocument(itemDocDTO);
    }

    public void addPostingDoc(ItemDocDTO itemDocDTO) {
        postingDocFactory.addDocument(itemDocDTO);
    }

    public void updatePostingDoc(ItemDocDTO itemDocDTO) {
        postingDocFactory.updateDocument(itemDocDTO);
    }

    public void addRequestDoc(ItemDocDTO itemDocDTO) {
        requestDocFactory.addDocument(itemDocDTO);
    }

    public void updateRequestDoc(ItemDocDTO itemDocDTO) {
        requestDocFactory.updateDocument(itemDocDTO);
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
}
