package com.example.sklad.factories;

import com.example.sklad.model.dto.ItemDocDTO;
import com.example.sklad.model.entities.documents.DocInterface;
import com.example.sklad.model.entities.documents.ItemDoc;
import lombok.Setter;

@Setter
public class ItemDocFactory implements DocFactory {

    ItemDocDTO itemDocDTO;

    @Override
    public ItemDoc createDocument() {

        ItemDoc itemDoc = new ItemDoc();

        return itemDoc;
    }

    @Override
    public DocInterface createDocumentFrom1C() {
        return null;
    }


}
