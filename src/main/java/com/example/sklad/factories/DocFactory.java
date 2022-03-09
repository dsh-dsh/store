package com.example.sklad.factories;

import com.example.sklad.model.entities.documents.DocInterface;
import org.springframework.stereotype.Component;

@Component
public interface DocFactory {

    DocInterface createDocument();
    DocInterface createDocumentFrom1C();

}
