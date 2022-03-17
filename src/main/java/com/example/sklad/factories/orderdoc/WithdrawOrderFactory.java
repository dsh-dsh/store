package com.example.sklad.factories.orderdoc;

import com.example.sklad.factories.abstraction.AbstractDocFactory;
import com.example.sklad.model.dto.documents.DocDTO;
import com.example.sklad.model.entities.documents.DocInterface;
import com.example.sklad.model.entities.documents.OrderDoc;
import com.example.sklad.model.enums.PaymentType;
import org.springframework.stereotype.Component;

@Component
public class WithdrawOrderFactory extends AbstractDocFactory {


    @Override
    public DocInterface addDocument(DocDTO docDTO) {
        OrderDoc order = getOrderDoc(docDTO);
        setAdditionalFieldsAndSave(order);

        return order;
    }

    @Override
    public DocInterface updateDocument(DocDTO docDTO) {
        OrderDoc order = getOrderDoc(docDTO);
        setAdditionalFieldsAndSave(order);

        return order;
    }

    private void setAdditionalFieldsAndSave(OrderDoc order) {
        order.setPaymentType(PaymentType.getByValue(docDTO.getPaymentType()));
        order.setAmount(docDTO.getAmount());
        order.setTax(docDTO.getTax());
        order.setIndividual(userService.getById(docDTO.getIndividual().getId()));
        order.setSupplier(companyService.getById(docDTO.getSupplier().getId()));
        orderDocRepository.save(order);
    }
}
