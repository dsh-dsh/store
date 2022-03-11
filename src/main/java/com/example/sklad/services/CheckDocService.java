package com.example.sklad.services;

import com.example.sklad.factories.CheckFactory;
import com.example.sklad.model.dto.requests.CheckRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CheckDocService {

    @Autowired
    private CheckFactory checkFactory;

    public void addCheckDocsFrom1C(CheckRequestDTO checkRequestDTO) {
        checkRequestDTO.getCheckDTOList()
                .forEach(itemDocDTO -> {
                    checkFactory.setItemDocDTO(itemDocDTO);
                    checkFactory.createDocumentFrom1C();
                });
    }

}
