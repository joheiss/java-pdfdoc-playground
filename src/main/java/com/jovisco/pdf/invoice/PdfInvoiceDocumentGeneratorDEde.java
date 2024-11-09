package com.jovisco.pdf.invoice;

import com.jovisco.pdf.core.RequestMap;

public class PdfInvoiceDocumentGeneratorDEde extends PdfInvoiceDocumentGenerator {

    public PdfInvoiceDocumentGeneratorDEde(
            RequestMap requestMap,
            String invoiceTemplateFilePath,
            String targetFilePath) {
        super(requestMap, invoiceTemplateFilePath, targetFilePath);
    }

    @Override
    protected void fillMetaInformation() {
        super.fillMetaInformation();
        var metadata = doc.getDocumentInformation();
        metadata.setKeywords("Jovisco, Invoice, " + invoiceId + ", DE_de");
    }
}
