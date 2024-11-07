package com.jovisco.invoicing.pdf;

import java.util.List;
import java.util.Map;

public class PdfInvoiceDocumentGeneratorDEde extends PdfInvoiceDocumentGenerator {

    public PdfInvoiceDocumentGeneratorDEde(
            Map<String, List<Object>> requestMap,
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
