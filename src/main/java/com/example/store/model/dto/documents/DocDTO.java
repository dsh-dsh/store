package com.example.store.model.dto.documents;

import com.example.store.model.dto.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocDTO {

    private int id;

    private int number;

//    private String time;

    @JsonProperty("date_time")
    private long dateTime;

    @JsonProperty("doc_type")
    private String docType;

    private ProjectDTO project;

    private UserDTO author;

    private UserDTO individual = new UserDTO();

    @JsonProperty("payment_type")
    private String paymentType;

    private float amount;
    private float tax;

    @JsonProperty("is_payed")
    private boolean isPayed;

    @JsonProperty("is_hold")
    private boolean isHold;

    @JsonProperty("is_deleted")
    private boolean isDeleted;

    @JsonProperty("is_delivery")
    private boolean isDelivery;

    private CompanyDTO supplier = new CompanyDTO();

    private CompanyDTO recipient = new CompanyDTO();

    @JsonProperty("storage_from")
    private StorageDTO storageFrom = new StorageDTO();

    @JsonProperty("storage_to")
    private StorageDTO storageTo = new StorageDTO();

    @JsonProperty("check_info")
    private CheckInfoDTO checkInfo;

    @JsonProperty("doc_items")
    private List<DocItemDTO> docItems;

    @JsonProperty("base_document_id")
    private int baseDocumentId;

}
