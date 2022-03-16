package com.example.sklad.factories;

import com.example.sklad.factories.abstraction.AbstractDocFactory;
import com.example.sklad.model.dto.documents.ItemDocDTO;
import com.example.sklad.model.entities.documents.DocInterface;
import com.example.sklad.model.entities.documents.OrderDoc;
import com.example.sklad.model.enums.DocumentType;
import com.example.sklad.model.enums.PaymentType;
import org.springframework.stereotype.Component;

@Component
public class CreditOrderFactory extends AbstractDocFactory {

    @Override
    public DocInterface addDocument(ItemDocDTO itemDocDTO) {
        this.docDTO = itemDocDTO;
        OrderDoc order = new OrderDoc();
        setDocumentType(DocumentType.CREDIT_ORDER_DOC);
        setCommonFields(order);
        setAdditionalFields(order);
        orderDocRepository.save(order);

        return order;
    }

    @Override
    public DocInterface updateDocument(ItemDocDTO itemDocDTO) {
        this.docDTO = itemDocDTO;
        OrderDoc order = getOrderDoc();
        updateCommonFields(order);
        setAdditionalFields(order);
        orderDocRepository.save(order);

        return order;
    }

    private void setAdditionalFields(OrderDoc order) {
        order.setPaymentType(PaymentType.getByValue(docDTO.getPaymentType()));
        order.setAmount(docDTO.getAmount());
        order.setTax(docDTO.getTax());
        order.setRecipient(companyService.getById(docDTO.getRecipient().getId()));
        order.setIndividual(userService.getById(docDTO.getIndividual().getId()));
        order.setSupplier(companyService.getById(docDTO.getSupplier().getId()));
    }
}
