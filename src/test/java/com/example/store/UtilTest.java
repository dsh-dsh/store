package com.example.store;

import com.example.store.controllers.TestService;
import com.example.store.model.dto.Item1CDTO;
import com.example.store.model.dto.ItemDTO;
import com.example.store.model.dto.PriceDTO;
import com.example.store.model.entities.Project;
import com.example.store.model.enums.PriceType;
import com.example.store.model.enums.Unit;
import com.example.store.model.enums.Workshop;
import com.example.store.utils.CollectionUtils;
import com.example.store.utils.Util;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class UtilTest extends TestService {

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
    void longToLocalDateTime() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        LocalDateTime oldTime = LocalDateTime.parse("2022-03-16 06:30:36.395", timeFormatter);
        long time = 1647401436395L;
        LocalDateTime newTime = Util.getLocalDateTime(time);
        assertEquals(oldTime, newTime);
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
    @Test
    void testString() {
        assertTrue(solution("aa", "aab"));
        assertTrue(solution("collectionUtils.intersection", "collecttionrtUtilrss.intesrsecstion"));
        assertFalse(solution("collectionUtils.intersection", "coecttionrtUtilrss.intesrsecstion"));
        assertFalse(solution("aa", "ab"));
    }

    boolean solution(String a, String b) {
        if(a.length() > b.length()) {
            return false;
        }
        Map<String, Integer> map = Stream.of(b.split(""))
                .collect(Collectors.toMap(Function.identity(), (i) -> 1, Integer::sum));

        for(String s : a.split("")) {
            if(!map.containsKey(s) || map.get(s) == 0) {
                return false;
            }
            map.compute(s, (k, v) -> --v);
        }
        return true;
    }

    private List<Item1CDTO> getItemDTOList() {
        List<Item1CDTO> list = new ArrayList<>();
        list.add(getItemDTO(3611, 1, "Cуп лапша (1)",
                getPrices(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                        180.00f, 220.00f)));
        list.add(getItemDTO(13, 11, "Ингредиент 2", List.of()));
        list.add(getItemDTO(444, 1, "Блюдо 10",
                getPrices(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                        100.00f, 120.00f)));
        list.add(getItemDTO(14, 1, "Блюдо 1",
                getPrices(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                        100.00f, 120.00f)));
        list.add(getItemDTO(12, 11, "Ингредиент 1", List.of()));
        list.add(getItemDTO(11, 2, "Бар", List.of()));
        return list;
    }

    private Item1CDTO getItemDTO(int number, int parentNumber, String name, List<PriceDTO> prices) {
        Item1CDTO dto = new Item1CDTO();
        dto.setName(name);
        dto.setPrintName(name);
        dto.setRegTime(Instant.now().toEpochMilli());
        dto.setUnit(getUnitDTO(Unit.PORTION));
        dto.setWorkshop(getWorkshopDTO(Workshop.KITCHEN));
        dto.setPrices(prices);
        dto.setNumber(number);
        dto.setParentNumber(parentNumber);
        return dto;
    }

    private List<PriceDTO> getPrices(long date, float retailValue, float deliveryValue){
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

    @Test
    void kWeakestRowsTest() {
        int[][] mat = new int[5][5];
        mat[0] = new int[] {1,1,0,0,0};
        mat[1] = new int[] {1,1,1,1,0};
        mat[2] = new int[] {1,0,0,0,0};
        mat[3] = new int[] {1,1,0,0,0};
        mat[4] = new int[] {1,1,1,1,1};

        int[] result = kWeakestRows(mat, 3);
        assertArrayEquals(new int[] {2,0,3}, result);

        mat = new int[4][4];
        mat[0] = new int[] {1,0,0,0};
        mat[1] = new int[] {1,1,1,1};
        mat[2] = new int[] {1,0,0,0};
        mat[3] = new int[] {1,0,0,0};

        result = kWeakestRows(mat, 2);
        assertArrayEquals(new int[] {0, 2}, result);
    }

    public int[] kWeakestRows(int[][] mat, int k) {
        int[][] arr = new int[mat.length][2];
        int[] output = new int[k];
        int strength = 0;
        for(int i = 0; i < mat.length; i++) {
            strength = 0;
            for(int j = 0; j < mat[i].length; j++) {
                strength += mat[i][j];
                if(mat[i][j] == 0) break;
            }
            arr[i][0] = strength;
            arr[i][1] = i;
        }
        sort(arr);
        for(int i = 0; i < k; i ++) {
            output[i] = arr[i][1];
        }
        return output;
    }

    public void sort(int[][] arr) {
        int[] temp;
        for(int i = arr.length - 1; i > 0; i--) {
            for(int j = 0; j < i; j++) {
                if(arr[j][0] > arr[j+1][0]) {
                    temp = arr[j];
                    arr[j] = arr[j+1];
                    arr[j+1] = temp;
                }
            }
        }
    }

    @Test
    void twoSumTest() {
        int[] result;
        int[] arr = new int[]{2,7,11,15};
        result = twoSum(arr, 9);
        assertArrayEquals(new int[] {0, 1}, result);

        arr = new int[]{3,4,2};
        result = twoSum(arr, 6);
        assertArrayEquals(new int[] {1, 2}, result);

        arr = new int[]{3,3};
        result = twoSum(arr, 6);
        assertArrayEquals(new int[] {0, 1}, result);

        arr = new int[]{125, 647, 125, 75, 37, 7823, 86258, 8366, 125};
        result = twoSum(arr, 250);
        assertArrayEquals(new int[] {0, 2}, result);
    }

    public int[] twoSum(int[] nums, int target) {
        HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
        int[] answer = new int[2];
        for (int i = 0; i < nums.length; i++) {
            if (!hashMap.containsKey(target - nums[i])) {
                hashMap.put(nums[i], i);
            }
            else {
                answer[0] = hashMap.get(target - nums[i]);
                answer[1] = i;
                break;
            }
        }
        return answer;
    }

    @Test
    void palindromeTest() {
        Solution solution = new Solution();
        Solution.ListNode n4 = solution.getNode(1, null);
        Solution.ListNode n3 = solution.getNode(2, n4);
        Solution.ListNode n2 = solution.getNode(2, n3);
        Solution.ListNode head = solution.getNode(1, n2);

        assertTrue(solution.isPalindrome(head));

        Solution.ListNode nn2 = solution.getNode(2, null);
        head = solution.getNode(1, nn2);

        assertFalse(solution.isPalindrome(head));

        Solution.ListNode nnn5 = solution.getNode(1, null);
        Solution.ListNode nnn4 = solution.getNode(2, nnn5);
        Solution.ListNode nnn3 = solution.getNode(3, nnn4);
        Solution.ListNode nnn2 = solution.getNode(2, nnn3);
        head = solution.getNode(1, nnn2);

        assertTrue(solution.isPalindrome(head));
    }

    class Solution {

        ListNode getNode(int val, ListNode node) {
            return new ListNode(val, node);
        }

        public boolean isPalindrome(ListNode head) {
            ListNode slow = head;
            ListNode fast = head;
            ListNode previous = null;
            while(fast != null && fast.next != null) {
                fast = fast.next.next;
                ListNode tmp = slow.next;
                slow.next = previous;
                previous = slow;
                slow = tmp;

            }
            if(fast != null) {
                slow = slow.next;
            }
            while(true) {
                if(slow.val != previous.val) {
                    return false;
                }
                if (slow.next == null) break;
                slow = slow.next;
                previous = previous.next;
            }
            return true;
        }

        public class ListNode {
            int val;
            ListNode next;

            ListNode() {
            }

            ListNode(int val) {
                this.val = val;
            }

            ListNode(int val, ListNode next) {
                this.val = val;
                this.next = next;
            }
        }

    }
}
