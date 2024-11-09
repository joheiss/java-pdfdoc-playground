package com.jovisco.invoicing.pdf;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

public class PdfInvoiceReferenceBlockGenerator extends PdfInvoiceBlockGenerator {

    public PdfInvoiceReferenceBlockGenerator(RequestMap requestMap, PDPageContentStream cs, float startY) {
        super(requestMap, cs, startY);
    }

    @Override
    public PdfElement generate() {
        posY = startY;
        var customerId = generateCustomerId();
        posY += 6.0f;
        var invoiceId = generateInvoiceId();
        posY += 6.0f;
        var invoiceDate = generateInvoiceDate();

        return new PdfBlock(customerId, invoiceId, invoiceDate);
    }

    protected PdfElement generateInvoiceId() {
        return PdfText.builder()
                .contentStream(cs)
                .text(requestMap.get(RequestMap.INVOICE_ID))
                .dimensions(PdfDimensions.ofA4mm(167.0f, 107.0f, 30.0f, 12.0f))
                .colorRGB(TEXT_COLOR)
                .build();
    }

    protected PdfElement generateCustomerId() {
        return PdfText.builder()
                .contentStream(cs)
                .text(requestMap.get(RequestMap.CUSTOMER_ID))
                .dimensions(PdfDimensions.ofA4mm(167.0f, 101.0f, 30.0f, 12.0f))
                .colorRGB(TEXT_COLOR)
                .build();
    }

    protected PdfElement generateInvoiceDate() {
        return PdfText.builder()
                .contentStream(cs)
                .text(requestMap.get(RequestMap.INVOICE_DATE))
                .dimensions(PdfDimensions.ofA4mm(167.0f, 113.0f, 30.0f, 12.0f))
                .colorRGB(TEXT_COLOR)
                .build();
    }
}
