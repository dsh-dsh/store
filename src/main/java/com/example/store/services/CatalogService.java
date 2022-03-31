package com.example.store.services;

import com.example.store.model.dto.CompanyDTO;
import com.example.store.model.dto.EnumDTO;
import com.example.store.model.enums.*;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CatalogService {


    public List<EnumDTO> getWorkshopDTOList() {
        return Arrays.stream(Workshop.values())
                .map(this::mapToEnum).collect(Collectors.toList());
    }

    public List<EnumDTO> getUnitsDTOList() {
        return Arrays.stream(Unit.values())
                .map(this::mapToEnum).collect(Collectors.toList());
    }

    public List<EnumDTO> getStorageTypeDTOList() {
        return Arrays.stream(StorageType.values())
                .map(this::mapToEnum).collect(Collectors.toList());
    }

    public List<EnumDTO> getQuantityTypeDTOList() {
        return Arrays.stream(QuantityType.values())
                .map(this::mapToEnum).collect(Collectors.toList());
    }

    public List<EnumDTO> getPaymentTypeDTOList() {
        return Arrays.stream(PaymentType.values())
                .map(this::mapToEnum).collect(Collectors.toList());
    }

    public List<EnumDTO> getDocumentTypeDTOList() {
        return Arrays.stream(DocumentType.values())
                .map(this::mapToEnum).collect(Collectors.toList());
    }

    public List<EnumDTO> getPriceTypeDTOList() {
        return Arrays.stream(PriceType.values())
                .map(this::mapToEnum).collect(Collectors.toList());
    }

    private EnumDTO mapToEnum(EnumeratedInterface data) {
        EnumDTO dto = new EnumDTO();
        dto.setName(data.toString());
        dto.setValue(data.getValue());
        return dto;
    }
}

