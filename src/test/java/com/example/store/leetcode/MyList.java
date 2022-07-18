package com.example.store.leetcode;

public class MyList <E> {

    int count = 0;
    Node head;
    Node current;

    public MyList() {
    }

    public MyList(E[] arr) {
        for(E element : arr) {
            add(element);
        }
    }

    public void add(E value) {
        Node next = new Node(value);
        if(head == null) {
            head = next;
            current = head;
        } else {
            current.next = next;
            current = next;
        }
        count++;
    }

    public boolean remove(E value) {
        if(head == null) {
            return false;
        } else {
            Node pointer = head;
            Node previous = null;
            while(pointer != null) {
                if(pointer.value == value) {
                    if(previous != null) {
                        previous.next = pointer.next;
                        if(previous.next == null) {
                            current = previous;
                        }
                    } else {
                        head = null;
                        current = null;
                    }
                    count--;
                    return true;
                }
                previous = pointer;
                pointer = pointer.next;
            }
        }
        return false;
    }

    public int size() {
        return count;
    }

    public Object getLastValue() {
        return current.value;
    }

    class Node <E> {
        E value;
        Node next;

        public Node(E value) {
            this.value = value;
        }

        public Node(E value, Node next) {
            this.value = value;
            this.next = next;
        }
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof MyList)) return false;
        MyList other = (MyList) o;
        Node thisPointer = this.head;
        Node otherPointer = other.head;
        while (thisPointer != null) {
            if(otherPointer == null) return false;
            if(thisPointer.value != otherPointer.value) return false;
            thisPointer = thisPointer.next;
            otherPointer = otherPointer.next;
        }
        if(otherPointer != null) return false;

        return true;
    }

    @Override
    public String toString() {
        Node node = this.head;
        String val = "";
        while(node != null) {
            val += node.value + ",";
            node = node.next;
        }
        return "ListNode [" +
                val.substring(0, val.length()-1) +
                "]";
    }
}
