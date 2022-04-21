package com.example.store;

import com.example.store.model.entities.Project;
import com.example.store.utils.CollectionUtils;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class UtilTest {

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

    @Test
    void updateCollectionTest() {

        Project project1 = mock(Project.class);
        Project project2 = mock(Project.class);
        Project project3 = mock(Project.class);
        Project project4 = mock(Project.class);
        Project project5 = mock(Project.class);
        Project project6 = mock(Project.class);
        Project project7 = mock(Project.class);
        Project project8 = mock(Project.class);
        Project project9 = mock(Project.class);
        Project project10 = mock(Project.class);

        CollectionUtils<Project> collectionUtils = new CollectionUtils<>();

        List<Project> current = List.of(project1, project2, project3, project4, project5, project6);
        List<Project> newList = List.of(project4, project5, project6, project7, project8, project9, project10);

        assertEquals(3, collectionUtils.intersection(current, newList).size());

        assertEquals(3, collectionUtils.intersection(newList, current).size());

        List<Project> result = collectionUtils.update(current, newList);
        assertEquals(7, result.size());
        assertEquals(project4, result.get(0));
        assertEquals(project10, result.get(6));
    }
}
