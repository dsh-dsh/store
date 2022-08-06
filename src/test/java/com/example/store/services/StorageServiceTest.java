package com.example.store.services;

import com.example.store.model.dto.StorageDTO;
import com.example.store.model.entities.Storage;
import com.example.store.model.enums.StorageType;
import com.example.store.repositories.StorageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties =
        "spring.datasource.url=jdbc:mysql://localhost:3306/skladtest")
@SpringBootTest
class StorageServiceTest {

    public static final String STORE_NAME = "Склад";
    public static final String STORE_TYPE = "STORE_STORE";
    public static final int ID = 1;
    public static final String STORE_NAME_4 = "Жаровня 4";
    public static final int ID_4 = 4;

    @Autowired
    private StorageService storageService;
    @Autowired
    private StorageRepository storageRepository;

    @Test
    void getStorageDTOListTest() {
        List<StorageDTO> list = storageService.getStorageDTOList();
        assertEquals(4, list.size());
        assertEquals(STORE_NAME, list.get(0).getName());
        assertEquals(StorageType.STORE_STORE.getValue(), list.get(0).getType());
    }

    @Test
    void getStorageListTest() {
        List<Storage> list = storageService.getStorageList();
        assertEquals(4, list.size());
    }

    @Test
    void getByIdTest() {
        Storage storage = storageService.getById(ID);
        assertEquals(STORE_NAME, storage.getName());
        assertEquals(StorageType.STORE_STORE, storage.getType());
    }

    @Test
    void getByNameTest() {
        Storage storage = storageService.getByName(STORE_NAME);
        assertEquals(ID, storage.getId());

    }

    @Test
    void mapToDTOTest() {
        Storage storage = storageService.getById(4);
        StorageDTO dto = storageService.mapToDTO(storage);
        assertEquals(ID_4, dto.getId());
        assertEquals(STORE_NAME_4, dto.getName());
        assertEquals( StorageType.CAFE_STORE.getValue(), dto.getType());
    }
}
