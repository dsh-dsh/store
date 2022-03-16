package com.example.sklad.services;

import com.example.sklad.factories.CreditOrderFactory;
import com.example.sklad.factories.WithdrawOrderFactory;
import com.example.sklad.model.dto.documents.ItemDocDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private WithdrawOrderFactory withdrawFactory;
    @Autowired
    private CreditOrderFactory creditOrderFactory;

    public void addRKO(ItemDocDTO docDTO) {
        withdrawFactory.addDocument(docDTO);
    }

    public void updateRKO(ItemDocDTO docDTO) {
        withdrawFactory.updateDocument(docDTO);
    }

    public void addPKO(ItemDocDTO docDTO) {
        creditOrderFactory.addDocument(docDTO);
    }

    public void updatePKO(ItemDocDTO docDTO) {
        creditOrderFactory.updateDocument(docDTO);
    }


}
