package com.example.store.controllers;

import com.example.store.model.dto.*;
import com.example.store.model.dto.documents.DocDTO;
import com.example.store.model.dto.requests.DocRequestDTO;
import com.example.store.model.enums.DocumentType;
import com.example.store.model.enums.Unit;
import com.example.store.model.enums.Workshop;
import com.example.store.utils.Constants;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class TestService {

    static final int ADD_VALUE = 0;
    static final int UPDATE_VALUE = 1;
    static final int DOC_ID = 1;
    static final int DOC_NUMBER = 11111;
    static final int CHECK_NUMBER = 654321;
    static final int RECEIPT_FIELDS_ID = 1;
    static final int AUTHOR_ID = 1;
    static final List<Integer> ADDED_ITEM_IDS = List.of(1, 2, 3, 4);
    static final List<Integer> UPDATE_ITEM_IDS = List.of(2, 3, 4, 5);
    static final int ONE_DOCUMENT = 1;
    static final int NO_DOCUMENTS = 0;
    static final String EXISTING_EMAIL = "customer@mail.ru";


    void addTo(DocDTO dto, int docId, int docNumber) {
        dto.setId(docId);
        dto.setNumber(docNumber);
    }

    DocRequestDTO setDTO(DocDTO docDTO) {
        DocRequestDTO dto = new DocRequestDTO();
        dto.setDocDTO(docDTO);
        return dto;
    }

    void setOrderFields(DocDTO dto, String type, float amount, float tax) {
        dto.setPaymentType(type);
        dto.setAmount(amount);
        dto.setTax(tax);
    }

    public DocDTO setDTOFields(DocumentType docType) {
        DocDTO dto = new DocDTO();
        dto.setDocType(docType.getValue());
        dto.setDateTime(ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault()).toInstant().toEpochMilli());
        dto.setProject(setProject(1));
        dto.setAuthor(setAuthorDTO(AUTHOR_ID));
        dto.setPayed(false);
        dto.setHold(false);

        return dto;
    }

    public CheckInfoDTO setCHeckInfo(int value) {
        CheckInfoDTO dto = new CheckInfoDTO();
        dto.setCheckNumber(CHECK_NUMBER + value);
        dto.setDateTime(ZonedDateTime
                .of(LocalDateTime.now(), ZoneId.systemDefault())
                .toInstant().toEpochMilli());
        dto.setAmountReceived(1000 * (value + 1));
        dto.setCashRegisterNumber(63214823871L);
        dto.setGuestNumber(1 + value);
        dto.setTableNumber(12 + value);
        dto.setWaiter("Официант 1" + value);
        dto.setReturn(value == 0);
        dto.setKKMChecked(value == 0);
        dto.setPayed(value == 0);
        dto.setPayedByCard(value == 0);
        dto.setDelivery(value == 0);

        return dto;
    }

    public CheckInfoDTO setCHeckInfo(int value, String time) {
        CheckInfoDTO dto = setCHeckInfo(value);
        dto.setTime(time);
        return dto;
    }

    public ProjectDTO setProject(int id) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setId(id);
        return projectDTO;
    }

    public ProjectDTO setProject(int id, String name) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setId(id);
        projectDTO.setName(name);
        return projectDTO;
    }

    public UserDTO setAuthorDTO(int id) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(id);
        return userDTO;
    }

    public UserDTO setAuthorDTO(int id, String name) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(id);
        userDTO.setName(name);
        return userDTO;
    }

    public UserDTO setIndividualDTO(int id) {
        UserDTO individualDTO = new UserDTO();
        individualDTO.setId(id);
        return individualDTO;
    }

    public UserDTO setIndividualDTO(int id, String name) {
        UserDTO individualDTO = new UserDTO();
        individualDTO.setId(id);
        individualDTO.setName(name);
        return individualDTO;
    }

    public UserDTO setIndividualDTO(int id, int code) {
        UserDTO individualDTO = new UserDTO();
        individualDTO.setCode(code);
        return individualDTO;
    }

    public CompanyDTO setCompanyDTO(int id) {
        CompanyDTO companyDTO = new CompanyDTO();
        companyDTO.setId(id);
        return companyDTO;
    }

    public CompanyDTO setCompanyDTO(int id, String name) {
        CompanyDTO companyDTO = new CompanyDTO();
        companyDTO.setId(id);
        companyDTO.setName(name);
        return companyDTO;
    }

    public CompanyDTO setCompanyDTO(String inn) {
        CompanyDTO companyDTO = new CompanyDTO();
        companyDTO.setInn(inn);
        return companyDTO;
    }

    public StorageDTO setStorageDTO(int id) {
        StorageDTO storageDTO = new StorageDTO();
        storageDTO.setId(id);
        return storageDTO;
    }

    public StorageDTO setStorageDTO(int id, String name) {
        StorageDTO storageDTO = new StorageDTO();
        storageDTO.setId(id);
        storageDTO.setName(name);
        return storageDTO;
    }

    public List<DocItemDTO> setDocItemDTOList(int value) {
        DocItemDTO first = new DocItemDTO();
        first.setItemId(ADDED_ITEM_IDS.get(0) + value);
        first.setPrice(10.00f * value);
        first.setQuantity(1 + value);
        DocItemDTO second = new DocItemDTO();
        second.setItemId(ADDED_ITEM_IDS.get(1) + value);
        second.setPrice(20.00f * value);
        second.setQuantity(2 + value);
        DocItemDTO third = new DocItemDTO();
        third.setItemId(ADDED_ITEM_IDS.get(2) + value);
        third.setPrice(30.00f * value);
        third.setQuantity(3 + value);
        DocItemDTO forth = new DocItemDTO();
        forth.setItemId(ADDED_ITEM_IDS.get(3) + value);
        forth.setPrice(40.00f * value);
        forth.setQuantity(4 + value);

        return List.of(first, second, third, forth);
    }

    protected EnumDTO getUnitDTO(Unit unit) {
        EnumDTO dto = new EnumDTO();
        dto.setName(unit.getValue());
        dto.setCode(unit.toString());
        return dto;
    }
    protected EnumDTO getWorkshopDTO(Workshop workshop) {
        EnumDTO dto = new EnumDTO();
        dto.setName(workshop.getValue());
        dto.setCode(workshop.toString());
        return dto;
    }
}
