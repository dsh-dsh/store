package com.example.store.factories.abstraction;

import com.example.store.model.dto.documents.DocDTO;
import com.example.store.model.entities.documents.DocInterface;
import com.example.store.model.entities.documents.Document;
import org.springframework.stereotype.Component;

@Component
public interface DocFactory {

    DocInterface addDocument(DocDTO docDTO);
    DocInterface updateDocument(DocDTO docDTO);
    void deleteDocument(int docId);
    boolean holdDocument(Document document);
    void unHoldDocument(Document document);

}
