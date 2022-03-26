package com.example.store.services;

import com.example.store.exceptions.BadRequestException;
import com.example.store.model.entities.Storage;
import com.example.store.repositories.StorageRepository;
import com.example.store.utils.Constants;
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
