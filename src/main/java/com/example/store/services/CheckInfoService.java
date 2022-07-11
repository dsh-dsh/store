package com.example.store.services;

import com.example.store.exceptions.BadRequestException;
import com.example.store.mappers.CheckInfoMapper;
import com.example.store.model.dto.CheckInfoDTO;
import com.example.store.model.entities.CheckInfo;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.repositories.CheckInfoRepository;
import com.example.store.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class CheckInfoService {

    public static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("MM.dd.yyyy HH:mm:ss");

    @Autowired
    private CheckInfoRepository checkInfoRepository;
    @Autowired
    private CheckInfoMapper checkInfoMapper;

    public CheckInfo getCheckInfo(ItemDoc check) {
        return checkInfoRepository.findByCheck(check)
                .orElseThrow(() -> new BadRequestException(Constants.NO_SUCH_CHECK_INFO_MESSAGE));
    }

    public void addCheckInfo(CheckInfoDTO checkInfoDTO, ItemDoc check) {
        CheckInfo checkInfo = new CheckInfo();
        setFields(checkInfoDTO, checkInfo);
        checkInfo.setCheck(check);

        checkInfoRepository.save(checkInfo);
    }

    public void updateCheckInfo(CheckInfoDTO checkInfoDTO, ItemDoc check) {
        CheckInfo checkInfo = getCheckInfo(check);
        setFields(checkInfoDTO, checkInfo);

        checkInfoRepository.save(checkInfo);
    }

    public void setFields(CheckInfoDTO checkInfoDTO, CheckInfo checkInfo) {
        checkInfo.setCheckNumber(checkInfoDTO.getCheckNumber());
        checkInfo.setCashRegisterNumber(checkInfoDTO.getCashRegisterNumber());
        checkInfo.setAmountReceived(checkInfoDTO.getAmountReceived());
        checkInfo.setGuestNumber(checkInfoDTO.getGuestNumber());
        checkInfo.setTableNumber(checkInfoDTO.getTableNumber());
        checkInfo.setWaiter(checkInfoDTO.getWaiter());
        checkInfo.setDateTime(Instant.ofEpochMilli(checkInfoDTO.getDateTime())
                .atZone(ZoneId.systemDefault()).toLocalDateTime());
        checkInfo.setReturn(checkInfoDTO.isReturn());
        checkInfo.setKKMChecked(checkInfoDTO.isKKMChecked());
        checkInfo.setPayed(checkInfoDTO.isPayed());
        checkInfo.setPayedByCard(checkInfoDTO.isPayedByCard());
        checkInfo.setDelivery(checkInfoDTO.isDelivery());
    }

    public void deleteByDoc(ItemDoc check) {
        checkInfoRepository.deleteByCheck(check);
    }

    public int countRowsByDoc(int docId) {
        return checkInfoRepository.countRowsByDocId(docId);
    }

    public CheckInfoDTO getCheckInfoDTO(ItemDoc doc) {
        CheckInfo checkInfo = getCheckInfo(doc);
        return checkInfoMapper.mapCheckInfo(checkInfo);
    }
}
