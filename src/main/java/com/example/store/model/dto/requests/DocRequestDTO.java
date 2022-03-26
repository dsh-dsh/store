package com.example.store.model.dto.requests;

import com.example.store.model.dto.documents.DocDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DocRequestDTO {
    @JsonProperty("item_doc_dto")
    private DocDTO docDTO;
}
