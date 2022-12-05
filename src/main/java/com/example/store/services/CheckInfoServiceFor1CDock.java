package com.example.store.services;

import com.example.store.model.dto.CheckInfoDTO;
import com.example.store.model.entities.CheckInfo;
import com.example.store.model.enums.CheckPaymentType;
import com.example.store.utils.Util;
import org.springframework.stereotype.Service;

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
        checkInfo.setDateTime(Util.getLocalDateTime(checkInfoDTO.getTime()));
        checkInfo.setReturn(checkInfoDTO.isReturn());
        checkInfo.setKKMChecked(checkInfoDTO.isKKMChecked());
        checkInfo.setPayed(checkInfoDTO.isPayed());
        checkInfo.setCheckPaymentType(CheckPaymentType.getByValue(checkInfoDTO.getCheckPaymentType()));
        checkInfo.setDelivery(checkInfoDTO.isDelivery());
    }

}
