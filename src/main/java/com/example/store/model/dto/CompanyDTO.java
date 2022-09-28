package com.example.store.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompanyDTO {

    private int id;

    private String name;

    private String inn;

    private int kpp;

    private List<AccountDTO> accounts;

    @JsonProperty("is_mine")
    private boolean isMine;

    private int code;
    private String phone;
    private String email;

}
