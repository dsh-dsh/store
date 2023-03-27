package com.example.store.components.doc_filters;

import com.example.store.model.enums.DocFilterType;
import com.example.store.model.enums.DocumentType;

import java.util.List;

public class StoreFilter extends AbstractDocFilter {
    @Override
    public List<DocumentType> getTypeList(DocFilterType type) {
        if(type == DocFilterType.STORE) {
            return List.of(DocumentType.RECEIPT_DOC, DocumentType.WRITE_OFF_DOC, DocumentType.MOVEMENT_DOC);
        } else {
            return nextFilter.getTypeList(type);
        }
    }
}
