package com.example.sklad.model.dto;

import com.example.sklad.model.entities.Company;
import com.example.sklad.model.entities.Storage;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostingDocDTO {

    private int id;

    private int number;

    private Timestamp time;

    private ProjectDTO project;

    private String type;

    private AuthorDTO author;

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

    private List<DocItemDTO> docItems;

}
