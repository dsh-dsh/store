package com.example.store.factories.orderdoc;

import com.example.store.factories.abstraction.AbstractDocFactory;
import com.example.store.model.dto.documents.DocDTO;
import com.example.store.model.entities.documents.DocInterface;
import com.example.store.model.entities.documents.OrderDoc;
import com.example.store.model.enums.PaymentType;
import org.springframework.stereotype.Component;

@Component
public class CreditOrderFactory extends AbstractDocFactory {

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
        order.setRecipient(companyService.getById(docDTO.getRecipient().getId()));
        order.setIndividual(userService.getById(docDTO.getIndividual().getId()));
        order.setSupplier(companyService.getById(docDTO.getSupplier().getId()));
        orderDocRepository.save(order);
    }

    @Override
    public void deleteDocument(int docId) {
        orderDocRepository.deleteById(docId);
    }
}