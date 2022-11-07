package com.example.store.controllers;

import com.example.store.model.dto.CompanyDTO;
import com.example.store.model.dto.ItemDTOForTree;
import com.example.store.model.responses.ListResponse;
import com.example.store.model.responses.Response;
import com.example.store.services.CompanyService;
import com.example.store.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/companies")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @GetMapping("/tree")
    public ResponseEntity<ListResponse<ItemDTOForTree>> getItemTree() {
        return ResponseEntity.ok(new ListResponse<>(companyService.getCompanyDTOTree()));
    }

    @GetMapping
    public ResponseEntity<Response<CompanyDTO>> getCompany(@RequestParam int id) {
        CompanyDTO company = companyService.getCompanyById(id);
        return ResponseEntity.ok(new Response<>(company));
    }

    @PostMapping
    public ResponseEntity<Response<String>> setCompany(@RequestBody CompanyDTO companyDTO) {
        companyService.setCompany(companyDTO);
        return ResponseEntity.ok(new Response<>(Constants.OK));
    }

    @PutMapping
    public ResponseEntity<Response<String>> updateCompany(@RequestBody CompanyDTO companyDTO) {
        companyService.updateCompany(companyDTO);
        return ResponseEntity.ok(new Response<>(Constants.OK));
    }

}
