package com.jovisco.tutorial.invoicingpdf;

import org.apache.pdfbox.pdmodel.PDDocument;

public class InvoicingPdfDocumentDEdeGenerator extends InvoicingPdfDocumentGenerator {

    public InvoicingPdfDocumentDEdeGenerator(
            CreatePdfInvoiceDocumentRequest request,
            String invoiceTemplateFilePath,
            String targetFilePath) {
        super(request, invoiceTemplateFilePath, targetFilePath);
    }

    @Override
    protected void fillMetaInformation() {
        super.fillMetaInformation();
        var metadata = doc.getDocumentInformation();
        metadata.setKeywords("Jovisco, Invoice, " + request.invoiceId() + ", DE_de");
    }
}
