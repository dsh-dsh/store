package com.example.store.components;

import com.example.store.model.entities.DocumentItem;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.services.DocItemService;
import com.example.store.services.DocumentService;
import com.example.store.services.LotMoveService;
import com.example.store.services.LotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UnHoldDocs {

    @Autowired
    private DocumentService documentService;
    @Autowired
    private LotService lotService;
    @Autowired
    private LotMoveService lotMoveService;
    @Autowired
    private DocItemService docItemService;

    //TODO add tests

    public void unHoldAllDocsAfter(Document document) {
        List<Document> documents = documentService.getDocumentsAfterAndInclude(document);
        documents.forEach(this::unHoldDocument);
    }

    public void unHoldDocument(Document document) {
        if (document instanceof ItemDoc) {
            lotMoveService.removeByDocument((ItemDoc) document);
            List<DocumentItem> items = docItemService.getItemsByDoc((ItemDoc) document);
            lotService.removeLots(items);
        }
        documentService.setHoldAndSave(false, document);
    }
}
