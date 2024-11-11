package com.jovisco.pdf.invoice;

import com.jovisco.pdf.base.PdfBaseTemplate;
import com.jovisco.pdf.base.PdfBaseTemplateGeneratorDEde;
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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PdfInvoiceTemplateTest {

    static final String templateFilePath = "target/test-invoicetemplate.pdf";
    RequestMap requestMap;

    @BeforeEach
    void setup() {
        requestMap = new RequestMap(makeTemplateRequestMap());
    }

    @Order(20)
    @Test
    @DisplayName("should create a base template pdf and save it at the given path")
    void test_create() {
        var baseTemplateGenerator = new PdfBaseTemplateGeneratorDEde(requestMap);
        var invoiceTemplateGenerator = new PdfInvoiceTemplateGeneratorDEde(requestMap);
        var template = new PdfInvoiceTemplate(baseTemplateGenerator, invoiceTemplateGenerator, templateFilePath);

        template.create();
    }

    @Order(21)
    @Test
    @DisplayName("should be accessible and contain predefined text blocks")
    void should_contain_predefined_text_blocks() {
        try (var doc = Loader.loadPDF(new File(templateFilePath))) {
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

    @Order(22)
    @Test
    @DisplayName("should throw an exception if target file cannot be saved")
    void should_throw_if_file_not_saved() {
        assertThrows(RuntimeException.class, () -> {
            var baseTemplateGenerator = new PdfBaseTemplateGeneratorDEde(requestMap);
            var invoiceTemplateGenerator = new PdfInvoiceTemplateGeneratorDEde(requestMap);
            var template = new PdfInvoiceTemplate(baseTemplateGenerator, invoiceTemplateGenerator, "NOACCESS/test-invoicetemplate.pdf");
            template.create();
        });
    }

    private Map<String, List<Object>> makeTemplateRequestMap() {
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
}