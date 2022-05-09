package com.example.store.model.dto.documents;

import com.example.store.model.dto.CompanyDTO;
import com.example.store.model.dto.ProjectDTO;
import com.example.store.model.dto.StorageDTO;
import com.example.store.model.dto.UserDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocToListDTO {

    private int id;

    private int number;

    private String type;

    private String time;

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
