package com.example.sklad.model.dto;

import com.example.sklad.model.entities.Company;
import com.example.sklad.model.entities.Storage;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemDocDTO {

    private int id;

    private int number;

    private Timestamp time;

    private ProjectDTO project;

    private String type;

    private AuthorDTO author;

    private IndividualDTO individual;

    @JsonProperty("is_payed")
    private boolean isPayed;

    @JsonProperty("is_hold")
    private boolean isHold;

    private CompanyDTO supplier;

    private CompanyDTO recipient;

    @JsonProperty("storage_from")
    private StorageDTO storageFrom;

    @JsonProperty("storage_to")
    private StorageDTO storageTo;

    @JsonProperty("doc_items")
    private List<DocItemDTO> docItems;

}
