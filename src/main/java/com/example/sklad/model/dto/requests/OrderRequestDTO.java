package com.example.sklad.model.dto.requests;

import com.example.sklad.model.dto.documents.OrderDTO;
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
    private List<OrderDTO> orderDTOList;
}