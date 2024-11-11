package com.jovisco.pdf.invoice;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class AppTest {

    static {
        System.setProperty("org.apache.commons.logging.Log",
                "org.apache.commons.logging.impl.NoOpLog");
    }


    @Nested
    @DisplayName("General App Tests")
    class GeneralAppTests {
        @Test
        void shouldWork() {
            assertTrue(true);
        }
    }
}