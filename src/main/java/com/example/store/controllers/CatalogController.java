package com.example.store.controllers;

import com.example.store.model.dto.*;
import com.example.store.model.responses.ListResponse;
import com.example.store.model.responses.Response;
import com.example.store.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/catalogs")
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

    @GetMapping("/storages")
    public ResponseEntity<ListResponse<StorageDTO>> getStorages() {
        List<StorageDTO> storageDTOList = storageService.getStorageDTOList();
        return ResponseEntity.ok(new ListResponse<>(storageDTOList));
    }

    @GetMapping("/projects")
    public ResponseEntity<ListResponse<ProjectDTO>> getProjects() {
        List<ProjectDTO> projectDTOList = projectService.getProjectDTOList();
        return ResponseEntity.ok(new ListResponse<>(projectDTOList));
    }

    @GetMapping("/companies")
    public ResponseEntity<ListResponse<CompanyDTO>> getCompanies() {
        List<CompanyDTO> companyDTOList = companyService.getCompanyDTOList();
        return ResponseEntity.ok(new ListResponse<>(companyDTOList));
    }

    @GetMapping("/users")
    public ResponseEntity<ListResponse<UserDTO>> getUsers() {
        List<UserDTO> userDTOList = userService.getUserDTOList(false);
        return ResponseEntity.ok(new ListResponse<>(userDTOList));
    }

    // todo add tests
    @GetMapping("/users/all")
    public ResponseEntity<ListResponse<UserDTO>> getAllUsers() {
        List<UserDTO> userDTOList = userService.getUserDTOList(true);
        return ResponseEntity.ok(new ListResponse<>(userDTOList));
    }

    @GetMapping("/workshops")
    public ResponseEntity<ListResponse<EnumDTO>> getWorkshops() {
        List<EnumDTO> workshopsDTOList = catalogService.getWorkshopDTOList();
        return ResponseEntity.ok(new ListResponse<>(workshopsDTOList));
    }

    @GetMapping("/units")
    public ResponseEntity<ListResponse<EnumDTO>> getUnits() {
        List<EnumDTO> unitsDTOList = catalogService.getUnitsDTOList();
        return ResponseEntity.ok(new ListResponse<>(unitsDTOList));
    }

    @GetMapping("/storage/types")
    public ResponseEntity<ListResponse<EnumDTO>> getStorageTypes() {
        List<EnumDTO> storageTypeDTOList = catalogService.getStorageTypeDTOList();
        return ResponseEntity.ok(new ListResponse<>(storageTypeDTOList));
    }

    @GetMapping("/quantity/types")
    public ResponseEntity<ListResponse<EnumDTO>> getQuantityTypes() {
        List<EnumDTO> quantityTypeDTOList = catalogService.getQuantityTypeDTOList();
        return ResponseEntity.ok(new ListResponse<>(quantityTypeDTOList));
    }

    @GetMapping("/payment/types")
    public ResponseEntity<ListResponse<EnumDTO>> getPaymentTypes() {
        List<EnumDTO> paymentTypeDTOList = catalogService.getPaymentTypeDTOList();
        return ResponseEntity.ok(new ListResponse<>(paymentTypeDTOList));
    }

    @GetMapping("/document/types")
    public ResponseEntity<ListResponse<EnumDTO>> getDocumentTypes() {
        List<EnumDTO> documentTypeDTOList = catalogService.getDocumentTypeDTOList();
        return ResponseEntity.ok(new ListResponse<>(documentTypeDTOList));
    }

    @GetMapping("/price/types")
    public ResponseEntity<ListResponse<EnumDTO>> getPriceTypes() {
        List<EnumDTO> priceTypeDTOList = catalogService.getPriceTypeDTOList();
        return ResponseEntity.ok(new ListResponse<>(priceTypeDTOList));
    }

    @GetMapping("/controller/advice/test/project")
    public ResponseEntity<Response<ProjectDTO>> getDocumentForControllerAdviceTest(@RequestParam int id) {
        ProjectDTO projectDTO = projectService.getProjectDTOById(id);
        return ResponseEntity.ok(new Response<>(projectDTO));
    }

}
