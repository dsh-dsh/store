package com.example.sklad.services;

import com.example.sklad.model.dto.documents.ItemDocDTO;
import com.example.sklad.model.responses.ListResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostingDocumentService {

//    private final ItemDocFactory docFactory = new ItemDocFactory();

    public ListResponse<ItemDocDTO> getDocuments() {
        return new ListResponse<>(List.of(new ItemDocDTO()), null);
    }

//    public ItemDoc createPostingDoc() {
//        return docFactory.createDocument();
//    }

}
