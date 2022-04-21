package com.example.store.utils;

import java.util.ArrayList;
import java.util.List;

public class CollectionUtils<E> {

    public List<E> union(List<E> current, List<E> newCollection) {
        List<E> result = new ArrayList<>(current);
        result.addAll(newCollection);
        return result;
    }

    public List<E> intersection(List<E> current, List<E> newCollection) {
        List<E> result = new ArrayList<>(current);
        result.retainAll(newCollection);
        return result;
    }

    public List<E> differance(List<E> current, List<E> newCollection) {
        List<E> result = new ArrayList<>(current);
        result.removeAll(newCollection);
        return result;
    }

    public List<E> update(List<E> current, List<E> newCollection) {
        List<E> oldElements = intersection(current, newCollection);
        List<E> newElements = differance(newCollection, oldElements);
        return union(oldElements, newElements);
    }

}
