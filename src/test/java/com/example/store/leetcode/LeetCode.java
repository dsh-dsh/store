package com.example.store.leetcode;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LeetCode {

    // NonDecreasingArray
    public boolean checkPossibility(int[] nums) {
        int count = 0;
        for (int i = 1; i < nums.length; i++) {
            if (nums[i - 1] > nums[i]) {
                if (++count > 1) return false;
                if (i - 2 <= 0 || nums[i - 2] <= nums[i]) {
                    nums[i - 1] = nums[i];
                } else {
                    nums[i] = nums[i - 1];
                }
            }
        }
        return count <= 1;
    }

    @Test
    void nonDecreasingArray() {
        int[] arr = new int[]{1, 5, 3, 4, 5};
        assertTrue(checkPossibility(arr));

        arr = new int[]{4, 2, 1};
        assertFalse(checkPossibility(arr));

        arr = new int[]{4, 2, 3};
        assertTrue(checkPossibility(arr));

        arr = new int[]{4, 5, 1, 8};
        assertTrue(checkPossibility(arr));
    }

    // two sum
    public int[] twoSum(int[] nums, int target) {
        int[] result = new int[2];
        Map<Integer, Integer> map = new HashMap<>();
        for(int i = 0; i < nums.length; i++) {
            if(map.containsKey(target - nums[i])) {
                result[0] = map.get(target - nums[i]);
                result[1] = i;
                return result;
            } else {
                map.put(nums[i], i);
            }
        }
        return result;
    }

    @Test
    void twoSumTest() {
        int[] arr = new int[] {2, 7, 11, 15};
        int[] res = new int[] {0, 1};
        assertArrayEquals(res, twoSum(arr, 9));

        arr = new int[] {3, 2, 4, 15};
        res = new int[] {1, 2};
        assertArrayEquals(res, twoSum(arr, 6));

        arr = new int[] {3, 2, 3, 15};
        res = new int[] {0, 2};
        assertArrayEquals(res, twoSum(arr, 6));
    }

    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode head = new ListNode();
        ListNode list = head;
        int val1, val2, res, ten = 0;
        while (true) {
            val1 = val2 = res = 0;
            if (l1 != null) {
                val1 = l1.val;
                l1 = l1.next;
            }
            if (l2 != null) {
                val2 = l2.val;
                l2 = l2.next;
            }
            res = val1 + val2 + ten;
            list.val = res % 10;
            ten = res / 10;

            if (l1 == null & l2 == null & ten == 0) break;

            list.next = new ListNode();
            list = list.next;
        }
        return head;
    }

    @Test
    void addTwoNumbersTest() {
        ListNode l1 = ListNode.of(new int[] {3,8,1});
        ListNode l2 = ListNode.of(new int[] {3,2,1});
        ListNode res = ListNode.of(new int[] {6,0,3});
        assertEquals(res, addTwoNumbers(l1, l2));

        l1 = ListNode.of(new int[] {9,9,9,9,9,9,9});
        l2 = ListNode.of(new int[] {9,9,9,9});
        res = ListNode.of(new int[] {8,9,9,9,0,0,0,1});
        assertEquals(res, addTwoNumbers(l1, l2));
    }

//    public int lengthOfLongestSubstring(String s) {
//        if(s.length() == 0) return 0;
//        Map<Character, Integer> map = new HashMap<>();
//        int start = 0;
//        int result = 0;
//        for(int i = 0; i < s.length(); i++) {
//            if(map.containsKey(s.charAt(i))) {
//                int strLength = i - start;
//                result = Math.max(result, strLength);
//                start = i = map.get(s.charAt(i))+1;
//                map.clear();
//            }
//            map.put(s.charAt(i), i);
//        }
//        result = Math.max(result, map.size());
//        return result;
//    }

    public int lengthOfLongestSubstring(String s) {
        int left = 0;
        int maxLength = 0;
        int[] key = new int[128];
        Arrays.fill(key,-1);
        for(int i=0;i<s.length();i++){
            int value = s.charAt(i);
            if(key[value] != -1){
                left = Math.max(key[value] + 1, left);
            }
            key[value] = i;
            maxLength = Math.max(maxLength, i - left + 1);
        }

        return maxLength;
    }

    @Test
    void lengthOfLongestSubstringTest() {
        String str = "abcabcbb";
        assertEquals(3, lengthOfLongestSubstring(str));
//
        str = "bbbbbbb";
        assertEquals(1, lengthOfLongestSubstring(str));

        str = "pwwkew";
        assertEquals(3, lengthOfLongestSubstring(str));

        str = "faagkaabcdefghijklmnafahfhk";
        assertEquals(14, lengthOfLongestSubstring(str));

        str = "f";
        assertEquals(1, lengthOfLongestSubstring(str));

        str = "fac";
        assertEquals(3, lengthOfLongestSubstring(str));

        str = "fa";
        assertEquals(2, lengthOfLongestSubstring(str));

        str = "abcdefghijklmn";
        assertEquals(14, lengthOfLongestSubstring(str));

        str = " ";
        assertEquals(1, lengthOfLongestSubstring(str));

        str = "";
        assertEquals(0, lengthOfLongestSubstring(str));

        str = "aab";
        assertEquals(2, lengthOfLongestSubstring(str));
    }

    public String convert(String s, int numRows) {
        if(numRows == 1) return s;
        String[] strArr = s.split("");
        String[] rows = new String[numRows];
        Arrays.fill(rows, "");
        int row = 0;
        int k = 1;
        for(int i = 0; i < strArr.length; i++) {
            rows[row] += strArr[i];
            row = row + k;
            if(row == 0) k = 1;
            if(row == numRows - 1) k = -1;
        }
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < rows.length; i++) {
            builder.append(rows[i]);
        }
        return builder.toString();
    }

    @Test
    void convertTest() {
        String s = "PAYPALISHIRING";
        assertEquals("PAHNAPLSIIGYIR", convert(s, 3));

        s = "PAYPALISHIRING";
        assertEquals("PINALSIGYAHRPI", convert(s, 4));

        s = "A";
        assertEquals("A", convert(s, 1));

        s = "AB";
        assertEquals("AB", convert(s, 3));
    }

    public int myAtoi(String s) {
        if (s == null || s.length() < 1) {
            return 0;
        }
        int sign = 1;
        int result = 0;
        int i = 0;
        while (i < s.length() && s.charAt(i) == ' ') {
            i++;
        }
        if(i < s.length() && (s.charAt(i) == '-' || s.charAt(i) == '+')) {
            sign = s.charAt(i) == '-' ? -1 : 1;
            i++;
        }
        while (i < s.length() && (s.charAt(i) >= '0' && s.charAt(i) <= '9')) {
            int digit = (s.charAt(i) - '0');
            if(result > Integer.MAX_VALUE/10 || (result == Integer.MAX_VALUE/10 && digit > Integer.MAX_VALUE%10)) {
                return sign == 1 ? Integer.MAX_VALUE : Integer.MIN_VALUE;
            }
            result = (result * 10) + digit;
            i++;
        }
        return result * sign;
    }

    @Test
    void myAtoiTest() {
        assertEquals(4193, myAtoi("   4193 with words"));

        assertEquals(-40193, myAtoi("   -40193 with words"));

        assertEquals(42, myAtoi("42"));

        assertEquals(2147483646, myAtoi("2147483646"));

        assertEquals(2147483647, myAtoi("2147483647"));

        assertEquals(-2147483647, myAtoi("-2147483647"));

        assertEquals(-2147483648, myAtoi("-2147483648"));

        assertEquals(-2147483648, myAtoi("-2147483649"));

        assertEquals(2147483647, myAtoi("2147483648"));
    }

    public long[] kthPalindrome(int[] queries, int length) {
        long[] answer = new long[queries.length];
        for (int i = 0; i < queries.length; i++) {
            answer[i] = getPalindrome(queries[i], length);
        }

        return answer;
    }

    private long getPalindrome(int position, int length) {
//        int steps = (length + 1) / 2;
        long steps = (length & 1)!=0 ? (length / 2) : (length/2 - 1);
        long palindrome = (long)Math.pow(10, steps);
        palindrome += position - 1;
        long result = palindrome;

        int a = length & 1;
        if ((length & 1) > 0) palindrome /= 10;

        while (palindrome > 0) {
            result = result * 10 + (palindrome % 10);
            palindrome /= 10;
        }
        String g = "";
        g += result;
        if(g.length() != length) return -1;

        return result;
    }

    @Test
    void kthPalindromeTest() {
        int[] query = new int[] {1,2,3,4,5,90};
        long[] answer = new long[] {101,111,121,131,141,999};
        assertArrayEquals(answer, kthPalindrome(query, 3));

        query = new int[] {1,2,3,4,5};
        answer = new long[] {1001,1111,1221,1331,1441};
        assertArrayEquals(answer, kthPalindrome(query, 4));
    }
}
