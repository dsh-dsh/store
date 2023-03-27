package com.example.store.components.doc_filters;

import com.example.store.model.enums.DocFilterType;
import com.example.store.model.enums.DocumentType;

import java.util.List;

public class RequestFilter extends AbstractDocFilter {
    @Override
    public List<DocumentType> getTypeList(DocFilterType type) {
        if(type == DocFilterType.REQUEST) {
            return List.of(DocumentType.REQUEST_DOC);
        } else {
            return nextFilter.getTypeList(type);
        }
    }
}
