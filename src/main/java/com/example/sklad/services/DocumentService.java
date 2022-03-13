package com.example.sklad.services;

import com.example.sklad.factories.CheckDocFactory;
import com.example.sklad.factories.PostingDocFactory;
import com.example.sklad.factories.ReceiptDocFactory;
import com.example.sklad.factories.RequestDocFactory;
import com.example.sklad.model.dto.documents.ItemDocDTO;
import com.example.sklad.model.responses.ListResponse;
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

    public void addCheckDoc(ItemDocDTO itemDocDTO) {
        checkDocFactory.addDocument(itemDocDTO);
    }

    public void updateCheckDoc(ItemDocDTO itemDocDTO) {
        checkDocFactory.updateDocument(itemDocDTO);
    }

    public void addReceiptDoc(ItemDocDTO itemDocDTO) {
        receiptDocFactory.addDocument(itemDocDTO);
    }

    public void addPostingDoc(ItemDocDTO itemDocDTO) {
        postingDocFactory.addDocument(itemDocDTO);
    }

    public void addRequestDoc(ItemDocDTO itemDocDTO) {
        requestDocFactory.addDocument(itemDocDTO);
    }

    public ListResponse<ItemDocDTO> getDocuments() {
        return new ListResponse<>(List.of(new ItemDocDTO()), null);

    }
}
