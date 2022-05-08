package com.example.store.components;

import com.example.store.factories.ItemDocFactory;
import com.example.store.factories.OrderDocFactory;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.services.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class ReHoldDocs {

    @Autowired
    private DocumentService documentService;
    @Autowired
    private ItemDocFactory itemDocFactory;
    @Autowired
    private OrderDocFactory orderDocFactory;

    public void reHolding(Document fromDoc, Document toDoc) {
        List<Document> documents = documentService.getDocumentsByPeriod(fromDoc, toDoc, false);
        documents.forEach(this::reHoldDocument);
    }

    public void reHoldDocument(Document document){
        if(document instanceof ItemDoc) {
            itemDocFactory.holdDocument(document);
        } else {
            orderDocFactory.holdDocument(document);
        }
    }


}
