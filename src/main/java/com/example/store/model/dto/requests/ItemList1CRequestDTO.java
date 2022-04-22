package com.example.store.model.dto.requests;

import com.example.store.model.dto.Item1CDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ItemList1CRequestDTO {
    @JsonProperty("item_dto_list")
    private List<Item1CDTO> item1CDTOList;
}
