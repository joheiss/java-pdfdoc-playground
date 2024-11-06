package com.jovisco.invoicing.pdf;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    }

    @Nested
    @DisplayName("Test Invoice Template")
    class InvoiceTemplateTests {

        @Test
        @DisplayName("should create the invoice template")
        void shouldCreateBInvoiceTemplate() throws IOException {
            var request = makePdfInvoiceTemplateRequest();
            var generator = new PdfInvoiceTemplateGeneratorDEde(
                    request,
                    "target/test-basetemplate.pdf",
                    "target/test-invoicetemplate.pdf");
            var template = generator.generate();
            assertNotNull(template);
        }
    }

    @Nested
    @DisplayName("Test Invoice Document")
    class InvoiceDocumentTests {

        @Test
        @DisplayName("should create a invoice document")
        void shouldCreateInvoiceDocument() {
            var request = makePdfInvoiceDocumentRequest();
            var generator = new PdfInvoiceDocumentGeneratorDEde(
                    request,
                    "target/test-invoicetemplate.pdf",
                    "target/test-invoice-" + request.invoiceId() + ".pdf");
            var template = generator.generate();
            assertNotNull(template);
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
                new String[]{"Jovisco AG", "Kapitalstr. 123", "", "12345 Berlin"},
                "Leistungszeitraum Oktober 2024",
                "12.345,67 €",
                "2.345,67 €",
                "15.678,90 €",
                new CreatePdfInvoiceItemRequest[]{
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
                },
                totalsHeader,
                "Zahlbar innerhalb von 30 Tagen ohne Abzug",
                new String[]{
                        "Bitte beachten sie den geänderten Mehrwertsteuersatz von nun 25%.",
                        "Der neue Mehrwertsteuersatz wird auf dieser Rechnung, entsprechend",
                        "dem gesetzlichen Stichtag, bereits angewendet."
                }
        );
    }
}