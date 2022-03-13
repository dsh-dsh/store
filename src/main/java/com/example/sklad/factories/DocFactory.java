package com.example.sklad.factories;

import com.example.sklad.model.dto.documents.ItemDocDTO;
import com.example.sklad.model.entities.documents.DocInterface;
import org.springframework.stereotype.Component;

@Component
public interface DocFactory {

    DocInterface addDocument(ItemDocDTO itemDocDTO);
    DocInterface updateDocument(ItemDocDTO itemDocDTO);

}
