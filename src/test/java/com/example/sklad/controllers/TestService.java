package com.example.sklad.controllers;

import com.example.sklad.model.dto.*;
import com.example.sklad.model.dto.documents.ItemDocDTO;
import com.example.sklad.model.dto.requests.ItemDocRequestDTO;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;

@Component
public class TestService {

    static final String URL_PREFIX = "/api/v1/docs";
    static final int ADD_VALUE = 0;
    static final int UPDATE_VALUE = 1;
    static final int DOC_ID = 1;
    static final int DOC_NUMBER = 11111;
    static final int CHECK_NUMBER = 654321;
    static final int RECEIPT_FIELDS_ID = 1;
    static final int AUTHOR_ID = 1;
    static final List<Integer> ADDED_ITEM_IDS = List.of(1, 2, 3, 4);
    static final List<Integer> UPDATE_ITEM_IDS = List.of(2, 3, 4, 5);

    void addTo(ItemDocDTO dto, int docId, int docNumber) {
        dto.setId(docId);
        dto.setNumber(docNumber);
    }

    ItemDocRequestDTO setDTO(ItemDocDTO itemDocDTO) {
        ItemDocRequestDTO dto = new ItemDocRequestDTO();
        dto.setItemDocDTO(itemDocDTO);
        return dto;
    }

    ItemDocDTO setRequestDocDTO() {
        ItemDocDTO dto = new ItemDocDTO();
        dto.setTime(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        dto.setProject(setProject(1));
        dto.setAuthor(setAuthorDTO(AUTHOR_ID));
        dto.setPayed(false);
        dto.setHold(false);
        dto.setStorageTo(setStorageDTO(1));

        return dto;
    }

    ItemDocDTO setPostingDocDTO() {
        ItemDocDTO dto = new ItemDocDTO();
        dto.setTime(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        dto.setProject(setProject(1));
        dto.setAuthor(setAuthorDTO(2));
        dto.setPayed(false);
        dto.setHold(false);
        dto.setRecipient(setCompanyDTO(1));
        dto.setStorageTo(setStorageDTO(1));

        return dto;
    }

    ItemDocDTO setReceiptDocDTO() {
        ItemDocDTO dto = new ItemDocDTO();
        dto.setTime(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        dto.setProject(setProject(RECEIPT_FIELDS_ID));
        dto.setAuthor(setAuthorDTO(2));
        dto.setPayed(true);
        dto.setHold(true);
        dto.setSupplier(setCompanyDTO(2));
        dto.setRecipient(setCompanyDTO(1));
        dto.setStorageTo(setStorageDTO(RECEIPT_FIELDS_ID));

        return dto;
    }

    ItemDocDTO setCheckDocDTO() {
        ItemDocDTO dto = new ItemDocDTO();
        dto.setTime(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        dto.setProject(setProject(3));
        dto.setAuthor(setAuthorDTO(2));
        dto.setPayed(true);
        dto.setHold(true);
        dto.setIndividual(setIndividualDTO(1));
        dto.setSupplier(setCompanyDTO(1));
        dto.setStorageFrom(setStorageDTO(3));

        return dto;
    }

    CheckInfoDTO setCHeckInfo(int value) {
        CheckInfoDTO dto = new CheckInfoDTO();
        dto.setCheckNumber(CHECK_NUMBER + value);
        dto.setDateTime(LocalDateTime.now());
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

    private ProjectDTO setProject(int id) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setId(id);
        return projectDTO;
    }

    private AuthorDTO setAuthorDTO(int id) {
        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setId(id);
        return authorDTO;
    }

    private IndividualDTO setIndividualDTO(int id) {
        IndividualDTO individualDTO = new IndividualDTO();
        individualDTO.setId(id);
        return individualDTO;
    }

    private CompanyDTO setCompanyDTO(int id) {
        CompanyDTO companyDTO = new CompanyDTO();
        companyDTO.setId(id);
        return companyDTO;
    }

    private StorageDTO setStorageDTO(int id) {
        StorageDTO storageDTO = new StorageDTO();
        storageDTO.setId(id);
        return storageDTO;
    }

    List<DocItemDTO> setDocItemDTOList(int value) {
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
}
