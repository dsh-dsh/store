package com.example.sklad.model.dto.documents;

import com.example.sklad.model.dto.CompanyDTO;
import com.example.sklad.model.dto.ProjectDTO;
import com.example.sklad.model.dto.StorageDTO;
import com.example.sklad.model.dto.UserDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocToListDTO {

    private int id;

    private int number;

    private long time;

    private ProjectDTO project;

    private UserDTO author;

    private CompanyDTO supplier;

    private StorageDTO storageFrom;

    private double amount;

    @JsonProperty("is_payed")
    private boolean isPayed;

    @JsonProperty("is_hold")
    private boolean isHold;

}
