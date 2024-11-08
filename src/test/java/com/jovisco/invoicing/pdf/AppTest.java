package com.jovisco.invoicing.pdf;


import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AppTest {

    @Nested
    @DisplayName("General App Tests")
    class GeneralAppTests {
        @Test
        void shouldWork() {
            assertTrue(true);
        }
    }

    @Nested
    @DisplayName("Test Base Template")
    class BaseTemplateTests {

        @Test
        @DisplayName("should create the base template")
        void shouldCreateBaseTemplate() {
            var generator = new PdfBaseTemplateGeneratorDEde("target/test-basetemplate.pdf");
            var template = generator.generate();
            assertNotNull(template);
        }

        @Test
        @DisplayName("should be accessible")
        void should_be_accessible() {
            try (var doc = Loader.loadPDF(new File("target/test-basetemplate.pdf"))) {
                var pdfStripper = new PDFTextStripper();
                //Retrieving text from PDF document
                var text = pdfStripper.getText(doc);
                System.out.println("Base Template Text Content: \n" + text);
                // contains at least a line feed
                assertFalse(text.isEmpty());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Nested
    @DisplayName("Test Invoice Template")
    class InvoiceTemplateTests {

        RequestMap requestMap = new RequestMap(makeTemplateRequestMap());

        @Test
        @DisplayName("should create the invoice template")
        void shouldCreateInvoiceTemplate() {
            var generator = new PdfInvoiceTemplateGeneratorDEde(
                    requestMap,
                    "target/test-basetemplate.pdf",
                    "target/test-invoicetemplate.pdf");
            var template = generator.generate();
            assertNotNull(template);
        }

        @Test
        @DisplayName("should be accessible")
        void should_be_accessible() {
            try (var doc = Loader.loadPDF(new File("target/test-invoicetemplate.pdf"))) {
                var pdfStripper = new PDFTextStripper();
                //Retrieving text from PDF document
                var text = pdfStripper.getText(doc);
                // contains at least the title, the reference block, and the items header
                assertFalse(text.isEmpty());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Test
        @DisplayName("should contain predefined text blocks")
        void should_contain_predefined_text_blocks() {
            try (var doc = Loader.loadPDF(new File("target/test-invoicetemplate.pdf"))) {
                var pdfStripper = new PDFTextStripper();
                //Retrieving text from PDF document
                var text = pdfStripper.getText(doc);
                // contains at least the title, the reference block, and the items header
                assertFalse(text.isEmpty());
                assertTrue(text.contains(requestMap.get(RequestMap.TITLE)));
                assertTrue(text.contains(requestMap.get(RequestMap.REF_TITLE)));
                assertTrue(text.contains(requestMap.get(RequestMap.CUSTOMER_ID_LBL)));
                assertTrue(text.contains(requestMap.get(RequestMap.INVOICE_ID_LBL)));
                assertTrue(text.contains(requestMap.get(RequestMap.INVOICE_DATE_LBL)));
                assertTrue(text.contains(requestMap.get(RequestMap.ITEM_ID_HDR)));
                assertTrue(text.contains(requestMap.get(RequestMap.ITEM_QTY_HDR)));
                assertTrue(text.contains(requestMap.get(RequestMap.ITEM_DESC_HDR)));
                assertTrue(text.contains(requestMap.get(RequestMap.ITEM_UNIT_NET_AMNT_HDR)));
                assertTrue(text.contains(requestMap.get(RequestMap.ITEM_TOTAL_NET_AMNT_HDR)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Nested
    @DisplayName("Test Invoice Document")
    class InvoiceDocumentTests {

        RequestMap requestMap = new RequestMap(makeDocumentRequestMap());
        String invoiceId = requestMap.get(RequestMap.INVOICE_ID);

        @Test
        @DisplayName("should create a invoice document")
        void shouldCreateInvoiceDocument() {
            var generator = new PdfInvoiceDocumentGeneratorDEde(
                    requestMap,
                    "target/test-invoicetemplate.pdf",
                    "target/test-invoice-" + invoiceId + ".pdf");
            var template = generator.generate();
            assertNotNull(template);
        }

        @Test
        @DisplayName("should be accessible")
        void should_be_accessible() {
            try (var doc = Loader.loadPDF(new File("target/test-R" + invoiceId + ".pdf"))) {
                var pdfStripper = new PDFTextStripper();
                //Retrieving text from PDF document
                var text = pdfStripper.getText(doc);
                // contains address lines, billing period,reference ids, item lines, totals, and payment terms
                assertFalse(text.isEmpty());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        @Test
        @DisplayName("should contain invoice data")
        void should_contain_invoice_data() {
            try (var doc = Loader.loadPDF(new File("target/test-R" + invoiceId + ".pdf"))) {
                var pdfStripper = new PDFTextStripper();
                //Retrieving text from PDF document
                var text = pdfStripper.getText(doc);
                // contains address lines, billing period,reference ids, item lines, totals, and payment terms
                var firstAddressLine = requestMap.getList(RequestMap.ADDRESS_LINES).getFirst();
                assertTrue(text.contains(firstAddressLine));
                assertTrue(text.contains(requestMap.get(RequestMap.BILLING_PERIOD)));
                assertTrue(text.contains(invoiceId));
                assertTrue(text.contains(requestMap.get(RequestMap.CUSTOMER_ID)));
                assertTrue(text.contains(requestMap.get(RequestMap.INVOICE_DATE)));
                var firstItem = requestMap.getItems().getFirst();
                assertTrue(text.contains(firstItem.get(RequestMap.ITEM_ID)));
                assertTrue(text.contains(firstItem.get(RequestMap.ITEM_QTY)));
                assertTrue(text.contains(firstItem.get(RequestMap.ITEM_DESC)));
                assertTrue(text.contains(requestMap.get(RequestMap.TOTAL_GROSS_AMNT)));
                assertTrue(text.contains(requestMap.get(RequestMap.PAYMENT_TERMS)));
                var firstOptionalInvoiceText = requestMap.getList(RequestMap.OPT_INVOICE_TEXTS).getFirst();
                assertTrue(firstOptionalInvoiceText != null && text.contains(firstOptionalInvoiceText));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static Map<String, List<Object>> makeTemplateRequestMap() {
        var requestMap = new HashMap<String, List<Object>>(15);

        requestMap.put(RequestMap.TITLE, List.of("Rechnung"));
        requestMap.put(RequestMap.REF_TITLE, List.of("Bitte bei Zahlung angeben:"));
        requestMap.put(RequestMap.CUSTOMER_ID_LBL, List.of("Kd.Nr."));
        requestMap.put(RequestMap.INVOICE_ID_LBL, List.of("Re.Nr."));
        requestMap.put(RequestMap.INVOICE_DATE_LBL, List.of("Datum"));
        requestMap.put(RequestMap.ITEM_ID_HDR, List.of("Pos"));
        requestMap.put(RequestMap.ITEM_QTY_HDR, List.of("Menge"));
        requestMap.put(RequestMap.ITEM_DESC_HDR, List.of("Beschreibung"));
        requestMap.put(RequestMap.ITEM_UNIT_NET_AMNT_HDR, List.of("Einzelpreis"));
        requestMap.put(RequestMap.ITEM_TOTAL_NET_AMNT_HDR, List.of("Gesamtpreis"));

        return requestMap;
    }

    private static Map<String, List<Object>> makeDocumentRequestMap() {
        var requestMap = new HashMap<String, List<Object>>(25);

        requestMap.put(RequestMap.INVOICE_ID, List.of("5222"));
        requestMap.put(RequestMap.INVOICE_DATE, List.of("2.11.2024"));
        requestMap.put(RequestMap.CUSTOMER_ID, List.of("4711"));
        requestMap.put(RequestMap.ADDRESS_LINES, List.of("Jovisco AG", "Kapitalstr. 123", "", "12345 Berlin"));
        requestMap.put(RequestMap.BILLING_PERIOD, List.of("Leistungszeitraum Oktober 2024"));
        requestMap.put(RequestMap.TOTAL_NET_AMNT_HDR, List.of("Nettobetrag"));
        requestMap.put(RequestMap.TOTAL_VAT_AMNT_HDR, List.of("19% MwSt"));
        requestMap.put(RequestMap.TOTAL_GROSS_AMNT_HDR, List.of("Bruttobetrag"));
        requestMap.put(RequestMap.TOTAL_NET_AMNT, List.of("12.345,67 €"));
        requestMap.put(RequestMap.TOTAL_VAT_AMNT, List.of("2.345,67 €"));
        requestMap.put(RequestMap.TOTAL_GROSS_AMNT, List.of("15.678,90 €"));
        requestMap.put(RequestMap.PAYMENT_TERMS, List.of("Zahlbar innerhalb von 30 Tagen ohne Abzug"));
        requestMap.put(RequestMap.OPT_INVOICE_TEXTS, List.of(
                "Bitte beachten sie den geänderten Mehrwertsteuersatz von nun 25%.",
                "Der neue Mehrwertsteuersatz wird auf dieser Rechnung, entsprechend",
                "dem gesetzlichen Stichtag, bereits angewendet."));

        requestMap.put(RequestMap.ITEMS,
                List.of(
                    Map.of(
                            RequestMap.ITEM_ID, "1",
                            RequestMap.ITEM_QTY, "123,5",
                            RequestMap.ITEM_DESC, "Projektstunden im Projekt \"Rettet die Welt\"",
                            RequestMap.ITEM_UNIT_NET_AMNT, "123,45 €",
                            RequestMap.ITEM_TOTAL_NET_AMNT, "12.345,67 €"
                    ),
                    Map.of(
                            RequestMap.ITEM_ID, "2",
                            RequestMap.ITEM_QTY, "23",
                            RequestMap.ITEM_DESC, "Reisestunden im Projekt \"Rettet die Welt\"",
                            RequestMap.ITEM_UNIT_NET_AMNT, "66,18 €",
                            RequestMap.ITEM_TOTAL_NET_AMNT, "1.234,56 €"
                    ),
                    Map.of(
                            RequestMap.ITEM_ID, "3",
                            RequestMap.ITEM_QTY, "2",
                            RequestMap.ITEM_DESC, "Übernachtungen im Hotel",
                            RequestMap.ITEM_UNIT_NET_AMNT, "150,00 €",
                            RequestMap.ITEM_TOTAL_NET_AMNT, "300,00 €"
                    ),
                    Map.of(
                            RequestMap.ITEM_ID, "4",
                            RequestMap.ITEM_QTY, "765",
                            RequestMap.ITEM_DESC, "Gefahrene Km",
                            RequestMap.ITEM_UNIT_NET_AMNT, "0,60 €",
                            RequestMap.ITEM_TOTAL_NET_AMNT, "488,00 €"
                    )
                ));

        return requestMap;
    }
}