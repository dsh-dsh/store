package com.example.sklad.factories;

import com.example.sklad.factories.abstraction.AbstractDocFactory;
import com.example.sklad.model.dto.documents.ItemDocDTO;
import com.example.sklad.model.entities.documents.DocInterface;
import com.example.sklad.model.entities.documents.ItemDoc;
import com.example.sklad.model.enums.DocumentType;
import org.springframework.stereotype.Component;

@Component
public class ReceiptDocFactory extends AbstractDocFactory {

    @Override
    public DocInterface addDocument(ItemDocDTO itemDocDTO) {
        this.docDTO = itemDocDTO;
        ItemDoc receiptDoc = new ItemDoc();
        setDocumentType(DocumentType.RECEIPT_DOC);
        setCommonFields(receiptDoc);
        setAdditionalFields(receiptDoc);
        itemDocRepository.save(receiptDoc);

        addDocItems(receiptDoc);

        return receiptDoc;
    }

    @Override
    public DocInterface updateDocument(ItemDocDTO itemDocDTO) {
        this.docDTO = itemDocDTO;
        ItemDoc receiptDoc = getItemDoc();
        updateCommonFields(receiptDoc);
        setAdditionalFields(receiptDoc);
        itemDocRepository.save(receiptDoc);

        updateDocItems(receiptDoc);

        return receiptDoc;
    }

    private void setAdditionalFields(ItemDoc receiptDoc) {
        receiptDoc.setSupplier(companyService.getById(docDTO.getSupplier().getId()));
        receiptDoc.setRecipient(companyService.getById(docDTO.getRecipient().getId()));
        receiptDoc.setStorageTo(storageService.getById(docDTO.getStorageTo().getId()));
    }

}
