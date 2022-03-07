package com.example.sklad.services;

import com.example.sklad.fabrics.ItemDocFactory;
import com.example.sklad.model.dto.PostingDocumentDTO;
import com.example.sklad.model.entities.documents.ItemMoveDoc;
import com.example.sklad.model.responses.ListResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostingDocumentService {

    private final ItemDocFactory docFactory = new ItemDocFactory();

    public ListResponse<PostingDocumentDTO> getDocuments() {
        ListResponse<PostingDocumentDTO> response =
                new ListResponse<>(List.of(new PostingDocumentDTO()), null);
        return response;
    }

    public ItemMoveDoc createPostingDoc() {
        ItemMoveDoc itemMoveDoc = docFactory.createDocument();
        return itemMoveDoc;
    }

}
