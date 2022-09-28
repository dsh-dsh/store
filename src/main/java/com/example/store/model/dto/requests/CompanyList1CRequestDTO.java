package com.example.store.model.dto.requests;


import com.example.store.model.dto.Company1CDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CompanyList1CRequestDTO {
    @JsonProperty("company_dto_list")
    private List<Company1CDTO> company1CDTOList;
}
