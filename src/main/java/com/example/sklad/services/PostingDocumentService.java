package com.example.sklad.services;

import com.example.sklad.factories.ItemDocFactory;
import com.example.sklad.model.dto.ItemDocDTO;
import com.example.sklad.model.entities.documents.ItemDoc;
import com.example.sklad.model.responses.ListResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostingDocumentService {

    private final ItemDocFactory docFactory = new ItemDocFactory();

    public ListResponse<ItemDocDTO> getDocuments() {
        ListResponse<ItemDocDTO> response =
                new ListResponse<>(List.of(new ItemDocDTO()), null);
        return response;
    }

    public ItemDoc createPostingDoc() {
        ItemDoc itemDoc = docFactory.createDocument();
        return itemDoc;
    }

}
