package com.example.sklad.factories;

import com.example.sklad.model.dto.documents.ItemDocDTO;
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


}
