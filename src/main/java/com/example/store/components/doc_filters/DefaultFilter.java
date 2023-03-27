package com.example.store.components.doc_filters;

import com.example.store.model.enums.DocFilterType;
import com.example.store.model.enums.DocumentType;

import java.util.List;

public class DefaultFilter extends AbstractDocFilter {
    @Override
    public List<DocumentType> getTypeList(DocFilterType type) {
        return List.of();
    }
}
