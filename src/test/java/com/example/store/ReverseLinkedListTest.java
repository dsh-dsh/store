package com.example.store;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReverseLinkedListTest {

    class ListNode<E> {
        E value;
        ListNode<E> next;

        public ListNode(E value, ListNode<E> next) {
            this.value = value;
            this.next = next;
        }
    }

    public ListNode<String> reverseLinkedList(ListNode<String> node) {
        ListNode<String> previous = null;
        ListNode<String> next = null;
        while(node.next != null) {
            next = node.next;
            node.next = previous;
            previous = node;
            node = next;
        }
        return node;
    }

    @Test
    void reverseLinkedListTest() {
        ListNode<String> d = new ListNode<>("d", null);
        ListNode<String> c = new ListNode<>("c", d);
        ListNode<String> b = new ListNode<>("b", c);
        ListNode<String> a = new ListNode<>("a", b);

        assertEquals(d, reverseLinkedList(a));
    }
}
