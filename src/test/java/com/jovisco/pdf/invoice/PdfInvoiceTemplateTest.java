package com.jovisco.pdf.invoice;

import com.jovisco.pdf.base.PdfBaseTemplateGeneratorDEde;
import com.jovisco.pdf.core.RequestMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class PdfInvoiceTemplateTest {

    RequestMap requestMap;

    @BeforeEach
    void setup() {
        requestMap = new RequestMap(makeTemplateRequestMap());
    }
    @Test
    @DisplayName("should create a base template pdf and save it at the given path")
    void test_create() {
        var templateFilePath = "target/test-invoicetemplate.pdf";
        var baseTemplateGenerator = new PdfBaseTemplateGeneratorDEde(requestMap);
        var invoiceTemplateGenerator = new PdfInvoiceTemplateGeneratorDEde(requestMap);
        var template = new PdfInvoiceTemplate(baseTemplateGenerator, invoiceTemplateGenerator, templateFilePath);

        template.create();
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