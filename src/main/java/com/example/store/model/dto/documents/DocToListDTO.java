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
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocToListDTO {

    private int id;

    private long number;

    @JsonProperty("doc_type")
    private String docType;

    @JsonProperty("date_time")
    private long dateTime;

    private ProjectDTO project;

    private UserDTO author;

    private CompanyDTO supplier;

    @JsonProperty("storage_from")
    private StorageDTO storageFrom;

    @JsonProperty("storage_to")
    private StorageDTO storageTo;

    private double amount;

    @JsonProperty("is_payed")
    private boolean isPayed;

    @JsonProperty("is_hold")
    private boolean isHold;

    @JsonProperty("is_deleted")
    private boolean isDeleted;

}
