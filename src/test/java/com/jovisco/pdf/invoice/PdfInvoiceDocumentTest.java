package com.jovisco.pdf.invoice;

import com.jovisco.pdf.base.PdfBaseTemplateGenerator_deDE;
import com.jovisco.pdf.core.RequestMap;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PdfInvoiceDocumentTest {

    RequestMap requestMap;

    @BeforeEach
    void setup() {
        requestMap = new RequestMap(makeDocumentRequestMap());
    }

    @Order(30)
    @Test
    @DisplayName("should create a invoice document pdf and save it at the given path")
    void test_create() {
        var invoiceId = requestMap.get(RequestMap.INVOICE_ID);
        var documentFilePath = "target/test-invoicedocument-" + invoiceId +".pdf";
        var baseTemplateGenerator = new PdfBaseTemplateGenerator_deDE(requestMap);
        var invoiceTemplateGenerator = new PdfInvoiceTemplateGenerator_deDE(requestMap);
        var invoiceDocumentGenerator = new PdfInvoiceDocumentGenerator_deDE(requestMap);
        var document = new PdfInvoiceDocument(
                baseTemplateGenerator, invoiceTemplateGenerator, invoiceDocumentGenerator, documentFilePath);

        document.create();
    }
    @Order(31)
    @Test
    @DisplayName("should be accessible and contain predefined text blocks")
    void should_contain_predefined_text_blocks() {
        var invoiceId = requestMap.get(RequestMap.INVOICE_ID);
        var documentFilePath = "target/test-invoicedocument-" + invoiceId +".pdf";
        try (var doc = Loader.loadPDF(new File(documentFilePath))) {
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

    @Order(32)
    @Test
    @DisplayName("should throw an exception if target file cannot be saved")
    void should_throw_if_file_not_saved() {
        assertThrows(RuntimeException.class, () -> {
            var invoiceId = requestMap.get(RequestMap.INVOICE_ID);
            var documentFilePath = "NOACCESS/test-invoicedocument-" + invoiceId +".pdf";
            var baseTemplateGenerator = new PdfBaseTemplateGenerator_deDE(requestMap);
            var invoiceTemplateGenerator = new PdfInvoiceTemplateGenerator_deDE(requestMap);
            var invoiceDocumentGenerator = new PdfInvoiceDocumentGenerator_deDE(requestMap);
            var document = new PdfInvoiceDocument(
                    baseTemplateGenerator, invoiceTemplateGenerator, invoiceDocumentGenerator, documentFilePath);
            document.create();
        });
    }

    private Map<String, Object> makeDocumentRequestMap() {
        var requestMap = new HashMap<String, Object>(40);
        // invoice template
        requestMap.put(RequestMap.TITLE, "Rechnung");
        requestMap.put(RequestMap.REF_TITLE, "Bitte bei Zahlung angeben:");
        requestMap.put(RequestMap.CUSTOMER_ID_LBL, "Kd.Nr.");
        requestMap.put(RequestMap.INVOICE_ID_LBL, "Re.Nr.");
        requestMap.put(RequestMap.INVOICE_DATE_LBL, "Datum");
        requestMap.put(RequestMap.ITEM_ID_HDR, "Pos");
        requestMap.put(RequestMap.ITEM_QTY_HDR, "Menge");
        requestMap.put(RequestMap.ITEM_DESC_HDR, "Beschreibung");
        requestMap.put(RequestMap.ITEM_UNIT_NET_AMNT_HDR, "Einzelpreis");
        requestMap.put(RequestMap.ITEM_TOTAL_NET_AMNT_HDR, "Gesamtpreis");
        // invoice document
        requestMap.put(RequestMap.INVOICE_ID, "5222");
        requestMap.put(RequestMap.INVOICE_DATE, "2.11.2024");
        requestMap.put(RequestMap.CUSTOMER_ID, "4711");
        requestMap.put(RequestMap.ADDRESS_LINES, List.of("Jovisco AG", "Kapitalstr. 123", "", "12345 Berlin"));
        requestMap.put(RequestMap.BILLING_PERIOD, "Leistungszeitraum Oktober 2024");
        requestMap.put(RequestMap.TOTAL_NET_AMNT_HDR, "Nettobetrag");
        requestMap.put(RequestMap.TOTAL_VAT_AMNT_HDR, "19% MwSt");
        requestMap.put(RequestMap.TOTAL_GROSS_AMNT_HDR, "Bruttobetrag");
        requestMap.put(RequestMap.TOTAL_NET_AMNT, "12.345,67 €");
        requestMap.put(RequestMap.TOTAL_VAT_AMNT, "2.345,67 €");
        requestMap.put(RequestMap.TOTAL_GROSS_AMNT, "15.678,90 €");
        requestMap.put(RequestMap.PAYMENT_TERMS, "Zahlbar innerhalb von 30 Tagen ohne Abzug");
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