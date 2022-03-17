package com.example.sklad.model.dto.requests;

import com.example.sklad.model.dto.documents.DocDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemDocListRequestDTO {
    @JsonProperty("item_doc_dto_list")
    private List<DocDTO> checkDTOList;
}
