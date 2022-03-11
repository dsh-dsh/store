package com.example.sklad.model.dto.requests;

import com.example.sklad.model.dto.ItemDocDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CheckRequestDTO {
    @JsonProperty("check_dto_list")
    private List<ItemDocDTO> checkDTOList;
}
