package com.example.sklad.services;

import com.example.sklad.model.dto.PostingDocumentDTO;
import com.example.sklad.model.responses.ListResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostingDocumentService {

    public ListResponse<PostingDocumentDTO> getDocuments() {

        ListResponse<PostingDocumentDTO> response =
                new ListResponse<>(List.of(new PostingDocumentDTO()), null);

        return response;
    }

}
