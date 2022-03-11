package com.example.sklad.services;

import com.example.sklad.exceptions.BadRequestException;
import com.example.sklad.model.entities.Storage;
import com.example.sklad.repositories.StorageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StorageService {

    @Autowired
    private StorageRepository storageRepository;

    public Storage getById(long id) {
        return storageRepository.findById(id)
                .orElseThrow(BadRequestException::new);
    }

    public Storage getByName(String name) {
        return storageRepository.findByNameIgnoreCase(name)
                .orElseThrow(BadRequestException::new);
    }

}
