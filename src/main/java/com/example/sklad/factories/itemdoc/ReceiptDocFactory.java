package com.example.sklad.factories.itemdoc;

import com.example.sklad.factories.abstraction.AbstractDocFactory;
import com.example.sklad.model.dto.documents.DocDTO;
import com.example.sklad.model.entities.documents.DocInterface;
import com.example.sklad.model.entities.documents.ItemDoc;
import org.springframework.stereotype.Component;

@Component
public class ReceiptDocFactory extends AbstractDocFactory {

    @Override
    public DocInterface addDocument(DocDTO docDTO) {
        ItemDoc receiptDoc = getItemDoc(docDTO);
        setAdditionalFieldsAndSave(receiptDoc);
        addDocumentItems(receiptDoc);

        return receiptDoc;
    }

    @Override
    public DocInterface updateDocument(DocDTO docDTO) {
        ItemDoc receiptDoc = getItemDoc(docDTO);
        setAdditionalFieldsAndSave(receiptDoc);
        updateDocItems(receiptDoc);

        return receiptDoc;
    }

    private void setAdditionalFieldsAndSave(ItemDoc receiptDoc) {
        receiptDoc.setSupplier(companyService.getById(docDTO.getSupplier().getId()));
        receiptDoc.setRecipient(companyService.getById(docDTO.getRecipient().getId()));
        receiptDoc.setStorageTo(storageService.getById(docDTO.getStorageTo().getId()));
        itemDocRepository.save(receiptDoc);
    }

}
