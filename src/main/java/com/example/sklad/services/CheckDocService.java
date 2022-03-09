package com.example.sklad.services;

import com.example.sklad.factories.CheckFactory;
import com.example.sklad.model.dto.requests.CheckRequestDTO;
import org.springframework.stereotype.Service;

@Service
public class CheckDocService {

    private final CheckFactory checkFactory = new CheckFactory();

    public void addCheckDocsFrom1C(CheckRequestDTO checkRequestDTO) {
        checkRequestDTO.getCheckDTOList()
                .forEach(itemDocDTO -> {
                    checkFactory.setItemDocDTO(itemDocDTO);
                    checkFactory.createDocumentFrom1C();
                });
    }

}
