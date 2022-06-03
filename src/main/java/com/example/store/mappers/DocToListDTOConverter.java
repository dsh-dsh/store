package com.example.store.mappers;

import com.example.store.model.dto.CompanyDTO;
import com.example.store.model.dto.ProjectDTO;
import com.example.store.model.dto.StorageDTO;
import com.example.store.model.dto.UserDTO;
import com.example.store.model.dto.documents.DocToListDTO;
import com.example.store.model.entities.Project;
import com.example.store.model.entities.Storage;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.entities.documents.OrderDoc;

public class DocToListDTOConverter {

    private static float amount = 0f;
    private static StorageDTO storageDTO;
    private static UserDTO authorDTO;
    private static CompanyDTO supplierDTO;

    private static void resetData() {
        amount = 0f;
        storageDTO = new StorageDTO();
        authorDTO = new UserDTO();
        supplierDTO = new CompanyDTO();
    }

    public static DocToListDTO convertToDTO(Document document){
        resetData();
        if(document instanceof OrderDoc) {
            amount = ((OrderDoc) document).getAmount();
        }
        if(document instanceof ItemDoc) {
            setStorageDTO((ItemDoc) document);
        }
        setAuthorDTO(document);
        setSupplier(document);
        DocToListDTO dto = new DocToListDTO();
        dto.setAuthor(authorDTO);
        dto.setId(document.getId());
        dto.setType(document.getDocType().getValue());
        dto.setNumber(document.getNumber());
        dto.setTime(document.getDateTime().toString());
        dto.setAmount(amount);
        dto.setStorageFrom(storageDTO);
        dto.setHold(document.isHold());
        dto.setPayed(document.isPayed());
        dto.setProject(getProjectDTO(document.getProject()));
        return dto;
    }

    private DocToListDTOConverter() {
    }

    private static ProjectDTO getProjectDTO(Project project) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setId(project.getId());
        projectDTO.setName(project.getName());
        return projectDTO;
    }

    private static void setStorageDTO(ItemDoc itemDoc) {
        if(itemDoc.getStorageFrom() != null) {
            Storage storage = itemDoc.getStorageFrom();
            storageDTO.setId(storage.getId());
            storageDTO.setName(storage.getName());
        }
    }

    private static void setAuthorDTO(Document document) {
        authorDTO.setId(document.getAuthor().getId());
        authorDTO.setEmail(document.getAuthor().getEmail());
        authorDTO.setName(document.getAuthor().getFirstName() + " " + document.getAuthor().getLastName());
    }

    private static void setSupplier(Document document) {
        if(document.getSupplier() != null) {
            supplierDTO.setId(document.getSupplier().getId());
            supplierDTO.setName(document.getSupplier().getName());
        }
    }

}
