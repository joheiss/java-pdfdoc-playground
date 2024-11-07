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

        Map<String, List<String>> requestMap = makePdfInvoiceTemplateRequest();

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
                assertTrue(text.contains(requestMap.get(PdfInvoiceTemplateRequest.TITLE).getFirst()));
                assertTrue(text.contains(requestMap.get(PdfInvoiceTemplateRequest.REF_TITLE).getFirst()));
                assertTrue(text.contains(requestMap.get(PdfInvoiceTemplateRequest.CUSTOMER_ID_LBL).getFirst()));
                assertTrue(text.contains(requestMap.get(PdfInvoiceTemplateRequest.INVOICE_ID_LBL).getFirst()));
                assertTrue(text.contains(requestMap.get(PdfInvoiceTemplateRequest.INVOICE_DATE_LBL).getFirst()));
                assertTrue(text.contains(requestMap.get(PdfInvoiceTemplateRequest.ITEM_ID_HDR).getFirst()));
                assertTrue(text.contains(requestMap.get(PdfInvoiceTemplateRequest.ITEM_QTY_HDR).getFirst()));
                assertTrue(text.contains(requestMap.get(PdfInvoiceTemplateRequest.ITEM_DESC_HDR).getFirst()));
                assertTrue(text.contains(requestMap.get(PdfInvoiceTemplateRequest.ITEM_UNIT_NET_AMNT_HDR).getFirst()));
                assertTrue(text.contains(requestMap.get(PdfInvoiceTemplateRequest.ITEM_TOTAL_NET_AMNT_HDR).getFirst()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Nested
    @DisplayName("Test Invoice Document")
    class InvoiceDocumentTests {

        Map<String, List<Object>> requestMap = makePdfInvoiceDocumentRequest();
        String invoiceId = String.valueOf(requestMap.get(PdfInvoiceDocumentRequest.INVOICE_ID).getFirst());

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
                var firstAddressLine = String.valueOf(requestMap.get(PdfInvoiceDocumentRequest.ADDRESS_LINES).getFirst());
                assertTrue(text.contains(firstAddressLine));
                assertTrue(text.contains(String.valueOf(requestMap.get(PdfInvoiceDocumentRequest.BILLING_PERIOD).getFirst())));
                assertTrue(text.contains(invoiceId));
                assertTrue(text.contains(String.valueOf(requestMap.get(PdfInvoiceDocumentRequest.CUSTOMER_ID).getFirst())));
                assertTrue(text.contains(String.valueOf(requestMap.get(PdfInvoiceDocumentRequest.INVOICE_DATE).getFirst())));
                Map<String, String> itemsMap = (Map<String, String>)requestMap.get(PdfInvoiceDocumentRequest.ITEMS).getFirst();
                assertTrue(text.contains(itemsMap.get(PdfInvoiceItemRequest.ITEM_DESC)));
                assertTrue(text.contains(String.valueOf(requestMap.get(PdfInvoiceDocumentRequest.TOTAL_GROSS_AMNT).getFirst())));
                assertTrue(text.contains(String.valueOf(requestMap.get(PdfInvoiceDocumentRequest.PAYMENT_TERMS).getFirst())));
                var firstOptionalInvoiceText = String.valueOf(requestMap.get(PdfInvoiceDocumentRequest.OPT_INVOICE_TEXTS).getFirst());
                assertTrue(firstOptionalInvoiceText != null && text.contains(firstOptionalInvoiceText));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static Map<String, List<String>> makePdfInvoiceTemplateRequest() {
        var requestMap = new HashMap<String, List<String>>(15);

        requestMap.put(PdfInvoiceTemplateRequest.TITLE, List.of("Rechnung"));
        requestMap.put(PdfInvoiceTemplateRequest.REF_TITLE, List.of("Bitte bei Zahlung angeben:"));
        requestMap.put(PdfInvoiceTemplateRequest.CUSTOMER_ID_LBL, List.of("Kd.Nr."));
        requestMap.put(PdfInvoiceTemplateRequest.INVOICE_ID_LBL, List.of("Re.Nr."));
        requestMap.put(PdfInvoiceTemplateRequest.INVOICE_DATE_LBL, List.of("Datum"));
        requestMap.put(PdfInvoiceTemplateRequest.ITEM_ID_HDR, List.of("Pos"));
        requestMap.put(PdfInvoiceTemplateRequest.ITEM_QTY_HDR, List.of("Menge"));
        requestMap.put(PdfInvoiceTemplateRequest.ITEM_DESC_HDR, List.of("Beschreibung"));
        requestMap.put(PdfInvoiceTemplateRequest.ITEM_UNIT_NET_AMNT_HDR, List.of("Einzelpreis"));
        requestMap.put(PdfInvoiceTemplateRequest.ITEM_TOTAL_NET_AMNT_HDR, List.of("Gesamtpreis"));

        return requestMap;
    }

    private static Map<String, List<Object>> makePdfInvoiceDocumentRequest() {
        Map<String, List<Object>> requestMap = new HashMap<>();
        requestMap.put(PdfInvoiceDocumentRequest.INVOICE_ID, List.of("5222"));
        requestMap.put(PdfInvoiceDocumentRequest.INVOICE_DATE, List.of("2.11.2024"));
        requestMap.put(PdfInvoiceDocumentRequest.CUSTOMER_ID, List.of("4711"));
        requestMap.put(PdfInvoiceDocumentRequest.ADDRESS_LINES, List.of("Jovisco AG", "Kapitalstr. 123", "", "12345 Berlin"));
        requestMap.put(PdfInvoiceDocumentRequest.BILLING_PERIOD, List.of("Leistungszeitraum Oktober 2024"));
//        requestMap.put(PdfInvoiceDocumentRequest.ITEMS,
//            List.of(
//                    new PdfInvoiceItemRequest(
//                            "1",
//                            "123,5",
//                            "Projektstunden im Projekt \"Rettet die Welt\"",
//                            "123,45 €",
//                            "12.345,67 €"),
//                    new PdfInvoiceItemRequest(
//                            "2",
//                            "23",
//                            "Reisestunden im Projekt \"Rettet die Welt\"",
//                            "66,18 €",
//                            "1.234,56 €"),
//                    new PdfInvoiceItemRequest(
//                            "3",
//                            "2",
//                            "Übernachtungen im Hotel",
//                            "150,00 €",
//                            "300,00 €"),
//                    new PdfInvoiceItemRequest(
//                            "4",
//                            "765",
//                            "Gefahrene Km",
//                            "0,60 €",
//                            "488,00 €")
//            ));
        requestMap.put(PdfInvoiceDocumentRequest.TOTAL_NET_AMNT_HDR, List.of("Nettobetrag"));
        requestMap.put(PdfInvoiceDocumentRequest.TOTAL_VAT_AMNT_HDR, List.of("19% MwSt"));
        requestMap.put(PdfInvoiceDocumentRequest.TOTAL_GROSS_AMNT_HDR, List.of("Bruttobetrag"));
        requestMap.put(PdfInvoiceDocumentRequest.TOTAL_NET_AMNT, List.of("12.345,67 €"));
        requestMap.put(PdfInvoiceDocumentRequest.TOTAL_VAT_AMNT, List.of("2.345,67 €"));
        requestMap.put(PdfInvoiceDocumentRequest.TOTAL_GROSS_AMNT, List.of("15.678,90 €"));
        requestMap.put(PdfInvoiceDocumentRequest.PAYMENT_TERMS, List.of("Zahlbar innerhalb von 30 Tagen ohne Abzug"));
        requestMap.put(PdfInvoiceDocumentRequest.OPT_INVOICE_TEXTS, List.of(
                "Bitte beachten sie den geänderten Mehrwertsteuersatz von nun 25%.",
                "Der neue Mehrwertsteuersatz wird auf dieser Rechnung, entsprechend",
                "dem gesetzlichen Stichtag, bereits angewendet."));

        requestMap.put(PdfInvoiceDocumentRequest.ITEMS,
                List.of(
                    Map.of(
                            PdfInvoiceItemRequest.ITEM_ID, "1",
                            PdfInvoiceItemRequest.ITEM_QTY, "123,5",
                            PdfInvoiceItemRequest.ITEM_DESC, "Projektstunden im Projekt \"Rettet die Welt\"",
                            PdfInvoiceItemRequest.ITEM_UNIT_NET_AMNT, "123,45 €",
                            PdfInvoiceItemRequest.ITEM_TOTAL_NET_AMNT, "12.345,67 €"
                    ),
                    Map.of(
                            PdfInvoiceItemRequest.ITEM_ID, "2",
                            PdfInvoiceItemRequest.ITEM_QTY, "23",
                            PdfInvoiceItemRequest.ITEM_DESC, "Reisestunden im Projekt \"Rettet die Welt\"",
                            PdfInvoiceItemRequest.ITEM_UNIT_NET_AMNT, "66,18 €",
                            PdfInvoiceItemRequest.ITEM_TOTAL_NET_AMNT, "1.234,56 €"
                    ),
                    Map.of(
                            PdfInvoiceItemRequest.ITEM_ID, "3",
                            PdfInvoiceItemRequest.ITEM_QTY, "2",
                            PdfInvoiceItemRequest.ITEM_DESC, "Übernachtungen im Hotel",
                            PdfInvoiceItemRequest.ITEM_UNIT_NET_AMNT, "150,00 €",
                            PdfInvoiceItemRequest.ITEM_TOTAL_NET_AMNT, "300,00 €"
                    ),
                    Map.of(
                            PdfInvoiceItemRequest.ITEM_ID, "4",
                            PdfInvoiceItemRequest.ITEM_QTY, "765",
                            PdfInvoiceItemRequest.ITEM_DESC, "Gefahrene Km",
                            PdfInvoiceItemRequest.ITEM_UNIT_NET_AMNT, "0,60 €",
                            PdfInvoiceItemRequest.ITEM_TOTAL_NET_AMNT, "488,00 €"
                    )
                ));

        return requestMap;
    }
}