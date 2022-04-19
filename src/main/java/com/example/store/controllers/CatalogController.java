package com.example.store.controllers;

import com.example.store.model.dto.*;
import com.example.store.model.responses.ListResponse;
import com.example.store.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CatalogController {

    @Autowired
    private CatalogService catalogService;
    @Autowired
    private StorageService storageService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private UserService userService;

    @GetMapping("/api/v1/catalogs/storages")
    public ResponseEntity<ListResponse<StorageDTO>> getStorages() {
        List<StorageDTO> storageDTOList = storageService.getStorageDTOList();
        return ResponseEntity.ok(new ListResponse<>(storageDTOList));
    }

    @GetMapping("/api/v1/catalogs/projects")
    public ResponseEntity<ListResponse<ProjectDTO>> getProjects() {
        List<ProjectDTO> projectDTOList = projectService.getProjectDTOList();
        return ResponseEntity.ok(new ListResponse<>(projectDTOList));
    }

    @GetMapping("/api/v1/catalogs/companies")
    public ResponseEntity<ListResponse<CompanyDTO>> getCompanies() {
        List<CompanyDTO> companyDTOList = companyService.getProjectDTOList();
        return ResponseEntity.ok(new ListResponse<>(companyDTOList));
    }

    @GetMapping("/api/v1/catalogs/users")
    public ResponseEntity<ListResponse<PersonDTO>> getUsers() {
        List<PersonDTO> projectDTOList = userService.getPersonDTOList();
        return ResponseEntity.ok(new ListResponse<>(projectDTOList));
    }

    @GetMapping("/api/v1/catalogs/workshops")
    public ResponseEntity<ListResponse<EnumDTO>> getWorkshops() {
        List<EnumDTO> workshopsDTOList = catalogService.getWorkshopDTOList();
        return ResponseEntity.ok(new ListResponse<>(workshopsDTOList));
    }

    @GetMapping("/api/v1/catalogs/units")
    public ResponseEntity<ListResponse<EnumDTO>> getUnits() {
        List<EnumDTO> unitsDTOList = catalogService.getUnitsDTOList();
        return ResponseEntity.ok(new ListResponse<>(unitsDTOList));
    }

    @GetMapping("/api/v1/catalogs/storage/types")
    public ResponseEntity<ListResponse<EnumDTO>> getStorageTypes() {
        List<EnumDTO> storageTypeDTOList = catalogService.getStorageTypeDTOList();
        return ResponseEntity.ok(new ListResponse<>(storageTypeDTOList));
    }

    @GetMapping("/api/v1/catalogs/quantity/types")
    public ResponseEntity<ListResponse<EnumDTO>> getQuantityTypes() {
        List<EnumDTO> quantityTypeDTOList = catalogService.getQuantityTypeDTOList();
        return ResponseEntity.ok(new ListResponse<>(quantityTypeDTOList));
    }

    @GetMapping("/api/v1/catalogs/payment/types")
    public ResponseEntity<ListResponse<EnumDTO>> getPaymentTypes() {
        List<EnumDTO> paymentTypeDTOList = catalogService.getPaymentTypeDTOList();
        return ResponseEntity.ok(new ListResponse<>(paymentTypeDTOList));
    }

    @GetMapping("/api/v1/catalogs/document/types")
    public ResponseEntity<ListResponse<EnumDTO>> getDocumentTypes() {
        List<EnumDTO> documentTypeDTOList = catalogService.getDocumentTypeDTOList();
        return ResponseEntity.ok(new ListResponse<>(documentTypeDTOList));
    }

    @GetMapping("/api/v1/catalogs/price/types")
    public ResponseEntity<ListResponse<EnumDTO>> getPriceTypes() {
        List<EnumDTO> priceTypeDTOList = catalogService.getPriceTypeDTOList();
        return ResponseEntity.ok(new ListResponse<>(priceTypeDTOList));
    }

}
