package com.example.sklad.factories;

import com.example.sklad.model.entities.documents.CashOrderDoc;
import com.example.sklad.model.entities.documents.DocInterface;

public class OrderFactory implements DocFactory {

    @Override
    public CashOrderDoc createDocument() {
        return null;
    }

    @Override
    public DocInterface createDocumentFrom1C() {
        return null;
    }

}
