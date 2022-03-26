package com.example.store.model.dto.requests;

import com.example.store.model.dto.documents.DocDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderRequestDTO {
    @JsonProperty("order_dto_list")
    private List<DocDTO> docDTO;
}