package com.example.store.factories;

import com.example.store.exceptions.BadRequestException;
import com.example.store.factories.abstraction.AbstractDocFactory;
import com.example.store.model.dto.documents.DocDTO;
import com.example.store.model.entities.documents.DocInterface;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.OrderDoc;
import com.example.store.model.enums.PaymentType;
import com.example.store.utils.Constants;
import org.springframework.stereotype.Component;

@Component
public class OrderDocFactory  extends AbstractDocFactory {

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
        if(docDTO.getRecipient().getId() != 0) {
            order.setRecipient(companyService.getById(docDTO.getRecipient().getId()));
        }
        order.setIndividual(userService.getById(docDTO.getIndividual().getId()));
//        order.setSupplier(companyService.getById(docDTO.getSupplier().getId()));
        orderDocRepository.save(order);
    }

    @Override
    public void deleteDocument(int docId) {
        OrderDoc order = orderDocRepository.findById(docId)
                .orElseThrow(() -> new BadRequestException(Constants.NO_SUCH_DOCUMENT_MESSAGE));
        // TODO при отмене удаления если ордер проведен, то проверить есть ли не проведенные до него документы
        order.setDeleted(!order.isDeleted());
        orderDocRepository.save(order);
    }

    @Override
    public void holdDocument(Document order) {
        order.setHold(true);
        orderDocRepository.save((OrderDoc) order);
    }

    @Override
    public void unHoldDocument(Document order){
        order.setHold(false);
        orderDocRepository.save((OrderDoc) order);
    }
}
