package com.example.sklad.model.dto.requests;

import com.example.sklad.model.dto.ItemDocDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckRequestDTO {
    private String string;
    private int integer;

    @JsonProperty("check_dto_list")
    private List<ItemDocDTO> checkDTOList;
}
