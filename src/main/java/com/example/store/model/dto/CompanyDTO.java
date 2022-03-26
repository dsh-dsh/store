package com.example.store.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompanyDTO {

    private int id;

    private String name;

    private long inn;

    private int kpp;

    private List<AccountDTO> accounts;

    @JsonProperty("is_mine")
    private boolean isMine;

}
