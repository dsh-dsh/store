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
public class AccountDTO {

    private long id;

    @JsonProperty("bank_name")
    private String bankName;

    @JsonProperty("bank_number")
    private int bankNumber;

    @JsonProperty("account_number")
    private String accountNumber;

    @JsonProperty("cor_account_number")
    private String corAccountNumber;

}