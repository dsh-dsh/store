package com.example.store.mappers;

import com.example.store.model.dto.CompanyDTO;
import com.example.store.model.dto.ProjectDTO;
import com.example.store.model.dto.StorageDTO;
import com.example.store.model.dto.UserDTO;
import com.example.store.model.dto.documents.DocToListDTO;
import com.example.store.model.entities.Company;
import com.example.store.model.entities.Project;
import com.example.store.model.entities.Storage;
import com.example.store.model.entities.User;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.entities.documents.OrderDoc;
import com.example.store.utils.Constants;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DocToListDTOConverter {

    public static DocToListDTO convertToDTO(Document document){
        DocToListDTO dto = new DocToListDTO();
        dto.setAuthor(getAuthorDTO(document.getAuthor()));
        dto.setId(document.getId());
        dto.setDocType(document.getDocType().getValue());
        dto.setNumber(document.getNumber());
        dto.setDateTime(getMillis(document.getDateTime()));
        dto.setHold(document.isHold());
        dto.setPayed(document.isPayed());
        dto.setDeleted(document.isDeleted());
        dto.setProject(getProjectDTO(document.getProject()));
        dto.setSupplier(getSupplier(document.getSupplier()));
        if(document instanceof OrderDoc) {
            dto.setAmount(((OrderDoc) document).getAmount());
        }
        if(document instanceof ItemDoc) {
            dto.setStorageFrom(getStorageDTO(((ItemDoc) document).getStorageFrom()));
            dto.setStorageTo(getStorageDTO(((ItemDoc) document).getStorageTo()));
        }
        return dto;
    }

    private static long getMillis(LocalDateTime time) {
        return ZonedDateTime.of(time, ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    private DocToListDTOConverter() {
    }

    private static ProjectDTO getProjectDTO(Project project) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setId(project.getId());
        projectDTO.setName(project.getName());
        return projectDTO;
    }

    private static StorageDTO getStorageDTO(Storage storage) {
        StorageDTO storageDTO = new StorageDTO();
        if(storage != null) {
            storageDTO.setId(storage.getId());
            storageDTO.setName(storage.getName());
        }
        return storageDTO;
    }

    private static UserDTO getAuthorDTO(User author) {
        UserDTO dto = new UserDTO();
        dto.setId(author.getId());
        dto.setEmail(author.getEmail());
        dto.setName(author.getFirstName() + " " + author.getLastName());
        return dto;
    }

    private static CompanyDTO getSupplier(Company company) {
        CompanyDTO dto = new CompanyDTO();
        if(company != null) {
            dto.setId(company.getId());
            dto.setName(company.getName());
        }
        return dto;
    }

}
