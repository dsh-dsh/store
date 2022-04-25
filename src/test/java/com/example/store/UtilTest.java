package com.example.store;

import com.example.store.model.dto.Item1CDTO;
import com.example.store.model.dto.ItemDTO;
import com.example.store.model.dto.PriceDTO;
import com.example.store.model.entities.Project;
import com.example.store.model.enums.PriceType;
import com.example.store.model.enums.Unit;
import com.example.store.model.enums.Workshop;
import com.example.store.utils.CollectionUtils;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class UtilTest {

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
    void RemovingListItemsInLoopTest() {
        List<ItemDTO> list = new ArrayList<>();
        list.add(new ItemDTO());
        list.add(new ItemDTO());
        list.add(new ItemDTO());
        ItemDTO itemDTO = new ItemDTO();
        itemDTO.setParentId(100);
        list.add(itemDTO);
        list.add(new ItemDTO());
        list.add(new ItemDTO());
        list.add(new ItemDTO());
        list.add(new ItemDTO());
        list.add(new ItemDTO());
        list.add(new ItemDTO());

        Iterator<ItemDTO> iterator = list.iterator();

        while(iterator.hasNext()) {
            if(iterator.next().getParentId() == 100) {
                iterator.remove();
            }
        }

        assertEquals(9, list.size());
    }

    @Test
    void Item1CDTOComparingTest() {
        List<Item1CDTO> list = getItemDTOList();
        list.sort(Comparator.comparing(Item1CDTO::getNumber));
        assertEquals(11, list.get(0).getNumber());
        assertEquals(3611, list.get(list.size()-1).getNumber());
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

    private List<Item1CDTO> getItemDTOList() {
        List<Item1CDTO> list = new ArrayList<>();
        list.add(getItemDTO(3611, 1, "Cуп лапша (1)",
                getPrices(LocalDate.now().toString(), 180.00f, 220.00f)));
        list.add(getItemDTO(13, 11, "Ингредиент 2", List.of()));
        list.add(getItemDTO(444, 1, "Блюдо 10",
                getPrices(LocalDate.now().toString(), 100.00f, 120.00f)));
        list.add(getItemDTO(14, 1, "Блюдо 1",
                getPrices(LocalDate.now().toString(), 100.00f, 120.00f)));
        list.add(getItemDTO(12, 11, "Ингредиент 1", List.of()));
        list.add(getItemDTO(11, 2, "Бар", List.of()));
        return list;
    }

    private Item1CDTO getItemDTO(int number, int parentNumber, String name, List<PriceDTO> prices) {
        Item1CDTO dto = new Item1CDTO();
        dto.setName(name);
        dto.setPrintName(name);
        dto.setRegTime(LocalDateTime.now().toString());
        dto.setUnit(Unit.PORTION.toString());
        dto.setWorkshop(Workshop.KITCHEN.toString());
        dto.setPrices(prices);
        dto.setNumber(number);
        dto.setParentNumber(parentNumber);
        return dto;
    }

    private List<PriceDTO> getPrices(String date, float retailValue, float deliveryValue){
        PriceDTO retailPrice = PriceDTO.builder()
                .date(date)
                .type(PriceType.RETAIL.getValue())
                .value(retailValue)
                .build();
        PriceDTO deliveryPrice = PriceDTO.builder()
                .date(date)
                .type(PriceType.DELIVERY.getValue())
                .value(deliveryValue)
                .build();
        return List.of(retailPrice, deliveryPrice);
    }
}
