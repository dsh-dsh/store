package com.example.store.services;

import com.example.store.model.dto.CheckInfoDTO;
import com.example.store.model.entities.CheckInfo;
import com.example.store.model.enums.CheckPaymentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class CheckInfoServiceFor1CDockTest {

    @Autowired
    private CheckInfoServiceFor1CDock service;

    @Test
    void setFieldsTest() {
        CheckInfoDTO dto = new CheckInfoDTO();
        CheckInfo checkInfo = new CheckInfo();
        dto.setCheckNumber(12345);
        dto.setCheckPaymentType(CheckPaymentType.QR_PAYMENT.getValue());
        dto.setTime("20.05.22 14:00:00");
        dto.setDelivery(true);

        service.setFields(dto, checkInfo);
        assertEquals(12345, checkInfo.getCheckNumber());
        assertEquals("2022-05-20T14:00", checkInfo.getDateTime().toString());
        assertEquals(CheckPaymentType.QR_PAYMENT, checkInfo.getCheckPaymentType());
        assertTrue(checkInfo.isDelivery());
    }
}