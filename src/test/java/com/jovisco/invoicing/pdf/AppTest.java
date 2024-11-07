package com.jovisco.invoicing.pdf;


import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

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

        CreatePdfInvoiceTemplateRequest request = makePdfInvoiceTemplateRequest();

        @Test
        @DisplayName("should create the invoice template")
        void shouldCreateInvoiceTemplate() {
            var request = makePdfInvoiceTemplateRequest();
            var generator = new PdfInvoiceTemplateGeneratorDEde(
                    request,
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
                assertTrue(text.contains(request.title()));
                assertTrue(text.contains(request.referenceTitle()));
                assertTrue(text.contains(request.customerIdLabel()));
                assertTrue(text.contains(request.invoiceIdLabel()));
                assertTrue(text.contains(request.invoiceDateLabel()));
                assertTrue(text.contains("Pos"));
                assertTrue(text.contains("Menge"));
                assertTrue(text.contains("Beschreibung"));
                assertTrue(text.contains("Einzelpreis"));
                assertTrue(text.contains("Gesamtpreis"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Nested
    @DisplayName("Test Invoice Document")
    class InvoiceDocumentTests {

        CreatePdfInvoiceDocumentRequest request = makePdfInvoiceDocumentRequest();

        @Test
        @DisplayName("should create a invoice document")
        void shouldCreateInvoiceDocument() {
            var generator = new PdfInvoiceDocumentGeneratorDEde(
                    request,
                    "target/test-invoicetemplate.pdf",
                    "target/test-invoice-" + request.invoiceId() + ".pdf");
            var template = generator.generate();
            assertNotNull(template);
        }

        @Test
        @DisplayName("should be accessible")
        void should_be_accessible() {
            try (var doc = Loader.loadPDF(new File("target/test-R" + request.invoiceId() + ".pdf"))) {
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
            try (var doc = Loader.loadPDF(new File("target/test-R" + request.invoiceId() + ".pdf"))) {
                var pdfStripper = new PDFTextStripper();
                //Retrieving text from PDF document
                var text = pdfStripper.getText(doc);
                // contains address lines, billing period,reference ids, item lines, totals, and payment terms                assertFalse(text.isEmpty());
                assertTrue(text.contains(request.addressLines().getFirst()));
                assertTrue(text.contains("Leistungszeitraum"));
                assertTrue(text.contains(request.invoiceId()));
                assertTrue(text.contains(request.customerId()));
                assertTrue(text.contains(request.formattedInvoiceDate()));
                assertTrue(text.contains(request.items().getFirst().description()));
                assertTrue(text.contains(request.formattedTotalGrossAmount()));
                assertTrue(text.contains(request.paymentTerms()));
                assertTrue(!request.optionalInvoiceTexts().isEmpty() &&
                        text.contains(request.optionalInvoiceTexts().getFirst()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static CreatePdfInvoiceTemplateRequest makePdfInvoiceTemplateRequest() {
        var textBuilder = new StringBuilder("Pos")
                .append("____")
                .append("Menge")
                .append("_____")
                .append("Beschreibung")
                .append("____________________________________")
                .append("Einzelpreis")
                .append("________")
                .append("Gesamtpreis");
        var columnsHeader = textBuilder
                .toString()
                .replaceAll("_", "  ");

        return new CreatePdfInvoiceTemplateRequest(
                "Rechnung",
                "Bitte bei Zahlung angeben:",
                "Kd.Nr.",
                "Re.Nr.",
                "Datum",
                columnsHeader);
    }

    private static CreatePdfInvoiceDocumentRequest makePdfInvoiceDocumentRequest() {
        var textBuilder = new StringBuilder("_______")
                .append("Nettobetrag")
                .append("______________________")
                .append("19% MwSt")
                .append("___________________________________")
                .append("Bruttobetrag");
        var totalsHeader = textBuilder
                .toString()
                .replaceAll("_", "  ");
        return new CreatePdfInvoiceDocumentRequest(
                "5222",
                "2.11.2024",
                "4711",
                 List.of("Jovisco AG", "Kapitalstr. 123", "", "12345 Berlin"),
                "Leistungszeitraum Oktober 2024",
                "12.345,67 €",
                "2.345,67 €",
                "15.678,90 €",
                 List.of(
                        new CreatePdfInvoiceItemRequest(
                                "1",
                                "123,5",
                                "Projektstunden im Projekt \"Rettet die Welt\"",
                                "123,45 €",
                                "12.345,67 €"),
                        new CreatePdfInvoiceItemRequest(
                                "2",
                                "23",
                                "Reisestunden im Projekt \"Rettet die Welt\"",
                                "66,18 €",
                                "1.234,56 €"),
                        new CreatePdfInvoiceItemRequest(
                                "3",
                                "2",
                                "Übernachtungen im Hotel",
                                "150,00 €",
                                "300,00 €"),
                        new CreatePdfInvoiceItemRequest(
                                "4",
                                "765",
                                "Gefahrene Km",
                                "0,60 €",
                                "488,00 €")
                ),
                totalsHeader,
                "Zahlbar innerhalb von 30 Tagen ohne Abzug",
                List.of(
                        "Bitte beachten sie den geänderten Mehrwertsteuersatz von nun 25%.",
                        "Der neue Mehrwertsteuersatz wird auf dieser Rechnung, entsprechend",
                        "dem gesetzlichen Stichtag, bereits angewendet."
                )
        );
    }
}