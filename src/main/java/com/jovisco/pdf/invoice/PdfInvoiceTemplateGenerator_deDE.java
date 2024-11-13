package com.jovisco.pdf.invoice;

import com.jovisco.pdf.core.RequestMap;

public class PdfInvoiceTemplateGenerator_deDE extends PdfInvoiceTemplateGenerator {

    public PdfInvoiceTemplateGenerator_deDE(RequestMap requestMap) {
        super(requestMap);
    }

    @Override
    public RequestMap getRequestMap() {
        return requestMap;
    }
}