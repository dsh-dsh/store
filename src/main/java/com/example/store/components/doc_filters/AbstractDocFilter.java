package com.example.store.components.doc_filters;

public abstract class AbstractDocFilter implements DocFilter {

    DocFilter nextFilter;

    @Override
    public void setNext(DocFilter filter) {
        this.nextFilter = filter;
    }

}
