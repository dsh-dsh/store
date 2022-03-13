package com.example.sklad.model.dto.requests;

import com.example.sklad.model.dto.documents.ItemDocDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemDocRequestDTO {
    @JsonProperty("item_doc_dto")
    private ItemDocDTO itemDocDTO;
}
