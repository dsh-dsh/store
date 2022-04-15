package com.example.store;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeleteMe {

    public Map<Integer, Integer> getIntegerMap(List<Integer> list) {
        return list.stream().collect(Collectors.toMap(
                Function.identity(),
                v -> 1,
                Integer::sum));
    }

    public Map<Integer, Long> getIntegerMapGroupingBy(List<Integer> list) {
        return list.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    @Test
    void getIntegerMapTest() {
        List<Integer> list = List.of(1,2,3,1,2,3);
        Map<Integer, Integer> map = getIntegerMap(list);
        assertEquals(2, (int) map.get(1));
        assertEquals(2, (int) map.get(2));
        assertEquals(2, (int) map.get(3));
    }

    @Test
    void getIntegerMapGroupingByTest() {
        List<Integer> list = List.of(1,2,3,1,2,3);
        Map<Integer, Long> map = getIntegerMapGroupingBy(list);
        assertEquals(2L, map.get(1));
        assertEquals(2L, map.get(2));
        assertEquals(2L, map.get(3));
    }
}
