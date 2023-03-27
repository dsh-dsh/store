package com.example.store.components.doc_filters;

import com.example.store.model.enums.DocFilterType;
import com.example.store.model.enums.DocumentType;

import java.util.List;

public interface DocFilter {
    void setNext(DocFilter filter);
    List<DocumentType> getTypeList(DocFilterType type);
}
