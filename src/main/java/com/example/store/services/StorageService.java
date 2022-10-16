package com.example.store.services;

import com.example.store.exceptions.BadRequestException;
import com.example.store.model.dto.StorageDTO;
import com.example.store.model.entities.Storage;
import com.example.store.repositories.StorageRepository;
import com.example.store.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StorageService {

    @Autowired
    private StorageRepository storageRepository;

    public List<StorageDTO> getStorageDTOList() {
        return getStorageList().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<Storage> getStorageList() {
        return storageRepository.findAll();
    }

    public Storage getById(int id) {
        return storageRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(
                        Constants.NO_SUCH_STORAGE_MESSAGE,
                        this.getClass().getName() + " - getById(int id)"));
    }

    public Storage getByName(String name) {
        return storageRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new BadRequestException(
                        String.format(Constants.NO_SUCH_STORAGE_MESSAGE, name),
                        this.getClass().getName() + " - getByName(String name)"));
    }

    public StorageDTO mapToDTO(Storage storage) {
        StorageDTO dto = new StorageDTO();
        dto.setId(storage.getId());
        dto.setName(storage.getName());
        dto.setType(storage.getType().getValue());
        return dto;
    }

}
