package com.example.store.components.doc_filters;

import com.example.store.model.enums.DocFilterType;
import com.example.store.model.enums.DocumentType;

import java.util.List;

public class InventFilter extends AbstractDocFilter {
    @Override
    public List<DocumentType> getTypeList(DocFilterType type) {
        if(type == DocFilterType.INVENT) {
            return List.of(DocumentType.INVENTORY_DOC);
        } else {
            return nextFilter.getTypeList(type);
        }
    }
}
