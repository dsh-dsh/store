package com.example.store.components.doc_filters;

import com.example.store.model.enums.DocFilterType;
import com.example.store.model.enums.DocumentType;

import java.util.List;

public class CheckFilter extends AbstractDocFilter {

    @Override
    public List<DocumentType> getTypeList(DocFilterType type) {
        if(type == DocFilterType.CHECK) {
            return List.of(DocumentType.CHECK_DOC);
        } else {
            return nextFilter.getTypeList(type);
        }
    }
}
