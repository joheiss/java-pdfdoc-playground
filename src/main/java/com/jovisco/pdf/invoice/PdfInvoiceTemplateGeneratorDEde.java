package com.jovisco.pdf.invoice;

import com.jovisco.pdf.core.RequestMap;

public class PdfInvoiceTemplateGeneratorDEde extends PdfInvoiceTemplateGenerator {

    public PdfInvoiceTemplateGeneratorDEde(RequestMap requestMap) {
        super(requestMap);
    }

    @Override
    public RequestMap getRequestMap() {
        return requestMap;
    }
}