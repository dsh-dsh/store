package com.example.sklad.services;

import com.example.sklad.exceptions.BadRequestException;
import com.example.sklad.model.entities.Storage;
import com.example.sklad.repositories.StorageRepository;
import com.example.sklad.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StorageService {

    @Autowired
    private StorageRepository storageRepository;

    public Storage getById(int id) {
        return storageRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(Constants.NO_SUCH_STORAGE_MESSAGE));
    }

    public Storage getByName(String name) {
        return storageRepository.findByNameIgnoreCase(name)
                .orElseThrow(BadRequestException::new);
    }

}
