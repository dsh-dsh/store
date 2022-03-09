package com.example.sklad.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocItemDTO {

    @JsonProperty("document_id")
    private long documentId;

    @JsonProperty("item_id")
    private long itemId;

    private double quantity;

    private double price;

    private double discount;

}
