package com.example.store.model.dto.requests;

import com.example.store.model.dto.DocItemDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DocItemDTORequest {
    @JsonProperty("doc_item_list")
    private List<DocItemDTO> docItemDTOList;
}
