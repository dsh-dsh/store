package com.example.store.services;

import com.example.store.model.dto.CheckInfoDTO;
import com.example.store.model.entities.CheckInfo;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class CheckInfoServiceFor1CDock extends CheckInfoService{


    @Override
    public void setFields(CheckInfoDTO checkInfoDTO, CheckInfo checkInfo) {
        checkInfo.setCheckNumber(checkInfoDTO.getCheckNumber());
        checkInfo.setCashRegisterNumber(checkInfoDTO.getCashRegisterNumber());
        checkInfo.setAmountReceived(checkInfoDTO.getAmountReceived());
        checkInfo.setGuestNumber(checkInfoDTO.getGuestNumber());
        checkInfo.setTableNumber(checkInfoDTO.getTableNumber());
        checkInfo.setWaiter(checkInfoDTO.getWaiter());
        checkInfo.setDateTime(Instant
                .ofEpochMilli(checkInfoDTO.getDateTime())
                .atZone(ZoneId.systemDefault()).toLocalDateTime());
        checkInfo.setReturn(checkInfoDTO.isReturn());
        checkInfo.setKKMChecked(checkInfoDTO.isKKMChecked());
        checkInfo.setPayed(checkInfoDTO.isPayed());
        checkInfo.setPayedByCard(checkInfoDTO.isPayedByCard());
        checkInfo.setDelivery(checkInfoDTO.isDelivery());
    }

}
