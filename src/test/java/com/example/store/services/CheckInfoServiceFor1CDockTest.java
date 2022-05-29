package com.example.store.services;

import com.example.store.model.dto.CheckInfoDTO;
import com.example.store.model.entities.CheckInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class CheckInfoServiceFor1CDockTest {

    @Autowired
    private CheckInfoServiceFor1CDock service;

    @Test
    void setFieldsTest() {
        CheckInfoDTO dto = new CheckInfoDTO();
        CheckInfo checkInfo = new CheckInfo();
        dto.setCheckNumber(12345);
        dto.setDateTime("22.05.20 14:00:00");
        dto.setDelivery(true);

        service.setFields(dto, checkInfo);
        assertEquals(12345, checkInfo.getCheckNumber());
        assertEquals("2022-05-20T14:00", checkInfo.getDateTime().toString());
        assertTrue(checkInfo.isDelivery());
    }
}