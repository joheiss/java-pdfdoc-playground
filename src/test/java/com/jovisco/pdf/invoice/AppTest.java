package com.jovisco.pdf.invoice;

import org.junit.jupiter.api.*;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;

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

        @Test
        void playWithLocale() {
            System.out.println(Locale.getDefault());
            var localeCode = Locale.getDefault().toString();
            System.out.println(localeCode);
        }

        @Test
        void playWithCurrency() {
            System.out.println(Locale.getDefault());
            var formatter = NumberFormat.getCurrencyInstance();
            System.out.println(formatter.format(1_234.56));
        }

        @Test
        void playWithResourceBundle() {
            var bundle = ResourceBundle.getBundle("i18n", Locale.getDefault());
            assertNotNull(bundle);
            assertEquals(bundle.getString("invoice"), "Rechnung");
        }
    }
}