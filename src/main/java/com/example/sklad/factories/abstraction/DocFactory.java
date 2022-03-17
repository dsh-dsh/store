package com.example.sklad.factories.abstraction;

import com.example.sklad.model.dto.documents.DocDTO;
import com.example.sklad.model.entities.documents.DocInterface;
import org.springframework.stereotype.Component;

@Component
public interface DocFactory {

    DocInterface addDocument(DocDTO docDTO);
    DocInterface updateDocument(DocDTO docDTO);
    DocInterface deleteDocument(DocDTO docDTO);

}
