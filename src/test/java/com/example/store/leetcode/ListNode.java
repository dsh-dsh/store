package com.example.store.leetcode;

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

    static ListNode of(int[] values) {
        if(values.length == 0) {
            return null;
        }
        ListNode head = new ListNode(values[0]);
        ListNode current = head;
        for(int i = 1; i < values.length; i++) {
            ListNode next = new ListNode(values[i]);
            current.next = next;
            current = next;
        }
        return head;
    }
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof ListNode)) return false;
        ListNode other = (ListNode) o;
        ListNode current = this;
        while (current != null) {
            if(other == null) return false;
            if(current.val != other.val) return false;
            current = current.next;
            other = other.next;
        }
        if(other != null) return false;

        return true;
    }

    @Override
    public String toString() {
        ListNode node = this;
        String val = "";
        while(node != null) {
            val += node.val + ",";
            node = node.next;
        }
        return "ListNode [" +
                val.substring(0, val.length()-1) +
                "]";
    }
}
