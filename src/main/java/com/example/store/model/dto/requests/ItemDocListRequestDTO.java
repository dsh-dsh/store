package com.example.store.model.dto.requests;

import com.example.store.model.dto.documents.DocDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ItemDocListRequestDTO {
    @JsonProperty("item_doc_dto_list")
    private List<DocDTO> docDTOList;
}
