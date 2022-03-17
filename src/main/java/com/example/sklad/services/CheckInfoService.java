package com.example.sklad.services;

import com.example.sklad.exceptions.BadRequestException;
import com.example.sklad.model.dto.CheckInfoDTO;
import com.example.sklad.model.entities.CheckInfo;
import com.example.sklad.model.entities.documents.ItemDoc;
import com.example.sklad.repositories.CheckInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CheckInfoService {

    @Autowired
    private CheckInfoRepository checkInfoRepository;

    public CheckInfo getCheckInfo(ItemDoc check) {
        return checkInfoRepository.findByCheck(check)
                .orElseThrow(BadRequestException::new);
    }

    public void addCheckInfo(CheckInfoDTO checkInfoDTO, ItemDoc check) {
        CheckInfo checkInfo = new CheckInfo();
        setFields(checkInfoDTO, checkInfo);
        checkInfo.setCheck(check);

        checkInfoRepository.save(checkInfo);
    }

    public void updateCheckInfo(CheckInfoDTO checkInfoDTO, ItemDoc check) {
        CheckInfo checkInfo = getCheckKKMInfo(check);
        setFields(checkInfoDTO, checkInfo);

        checkInfoRepository.save(checkInfo);
    }

    private CheckInfo getCheckKKMInfo(ItemDoc check) {
        return checkInfoRepository.findByCheck(check)
                .orElseThrow(BadRequestException::new);
    }

    private void setFields(CheckInfoDTO checkInfoDTO, CheckInfo checkInfo) {
        checkInfo.setCheckNumber(checkInfoDTO.getCheckNumber());
        checkInfo.setCashRegisterNumber(checkInfoDTO.getCashRegisterNumber());
        checkInfo.setAmountReceived(checkInfoDTO.getAmountReceived());
        checkInfo.setGuestNumber(checkInfoDTO.getGuestNumber());
        checkInfo.setTableNumber(checkInfoDTO.getTableNumber());
        checkInfo.setWaiter(checkInfoDTO.getWaiter());
        checkInfo.setDateTime(checkInfoDTO.getDateTime());
        checkInfo.setReturn(checkInfoDTO.isReturn());
        checkInfo.setKKMChecked(checkInfoDTO.isKKMChecked());
        checkInfo.setPayed(checkInfoDTO.isPayed());
        checkInfo.setPayedByCard(checkInfoDTO.isPayedByCard());
        checkInfo.setDelivery(checkInfoDTO.isDelivery());
    }

    public void deleteByDocId(ItemDoc check) {
        checkInfoRepository.deleteByCheck(check);
    }

    public int countRowsByDoc(int docId) {
        return checkInfoRepository.countRowsByDocId(docId);
    }
}
