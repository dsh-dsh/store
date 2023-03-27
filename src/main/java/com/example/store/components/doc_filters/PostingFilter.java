package com.example.store.components.doc_filters;

import com.example.store.model.enums.DocFilterType;
import com.example.store.model.enums.DocumentType;

import java.util.List;

public class PostingFilter extends AbstractDocFilter {

    @Override
    public List<DocumentType> getTypeList(DocFilterType type) {
        if(type == DocFilterType.POSTING) {
            return List.of(DocumentType.POSTING_DOC);
        } else {
            return nextFilter.getTypeList(type);
        }
    }
}
