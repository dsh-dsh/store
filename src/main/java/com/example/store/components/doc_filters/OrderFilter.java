package com.example.store.components.doc_filters;

import com.example.store.model.enums.DocFilterType;
import com.example.store.model.enums.DocumentType;

import java.util.List;

public class OrderFilter extends AbstractDocFilter {
    @Override
    public List<DocumentType> getTypeList(DocFilterType type) {
        if(type == DocFilterType.ORDER) {
            return List.of(DocumentType.WITHDRAW_ORDER_DOC, DocumentType.CREDIT_ORDER_DOC);
        } else {
            return nextFilter.getTypeList(type);
        }
    }
}
