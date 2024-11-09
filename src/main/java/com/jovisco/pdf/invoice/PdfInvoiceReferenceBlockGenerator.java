package com.jovisco.pdf.invoice;

import com.jovisco.pdf.core.*;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

public class PdfInvoiceReferenceBlockGenerator extends PdfBlockGenerator {

    public PdfInvoiceReferenceBlockGenerator(RequestMap requestMap, PDPageContentStream cs, PdfPosY posY) {
        super(requestMap, cs, posY);
    }

    @Override
    public PdfElement generate() {
        var customerId = generateCustomerId();
        posY.incrementBy(6.0f);
        var invoiceId = generateInvoiceId();
        posY.incrementBy(6.0f);
        var invoiceDate = generateInvoiceDate();

        return new PdfBlock(customerId, invoiceId, invoiceDate);
    }

    protected PdfElement generateInvoiceId() {
        return PdfText.builder()
                .contentStream(cs)
                .text(requestMap.get(RequestMap.INVOICE_ID))
                .dimensions(PdfDimensions.ofA4mm(167.0f, posY.getY(), 30.0f, 12.0f))
                .colorRGB(TEXT_COLOR)
                .build();
    }

    protected PdfElement generateCustomerId() {
        return PdfText.builder()
                .contentStream(cs)
                .text(requestMap.get(RequestMap.CUSTOMER_ID))
                .dimensions(PdfDimensions.ofA4mm(167.0f, posY.getY(), 30.0f, 12.0f))
                .colorRGB(TEXT_COLOR)
                .build();
    }

    protected PdfElement generateInvoiceDate() {
        return PdfText.builder()
                .contentStream(cs)
                .text(requestMap.get(RequestMap.INVOICE_DATE))
                .dimensions(PdfDimensions.ofA4mm(167.0f, posY.getY(), 30.0f, 12.0f))
                .colorRGB(TEXT_COLOR)
                .build();
    }
}
