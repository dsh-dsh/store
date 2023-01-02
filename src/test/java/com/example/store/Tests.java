package com.example.store;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Tests {

    @Test
    void floatTest1() {
        assertEquals(0.208f, (100 / (float)(480 * 1)), 0.001);
    }

}
