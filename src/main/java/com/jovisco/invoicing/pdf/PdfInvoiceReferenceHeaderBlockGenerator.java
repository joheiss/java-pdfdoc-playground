package com.jovisco.invoicing.pdf;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

public class PdfInvoiceReferenceHeaderBlockGenerator extends PdfInvoiceBlockGenerator {

    protected final static float REFERENCE_UNDERLINE_WIDTH = 38.0f * PdfDimensions.PAGE_WIDTH_FACTOR;

    public PdfInvoiceReferenceHeaderBlockGenerator(RequestMap requestMap, PDPageContentStream cs, float startY) {
        super(requestMap, cs, startY);
    }

    @Override
    public PdfElement generate() {
        posY = startY;
        var refTitle = generateReferenceTitle();
        posY += 2.0f;
        var refUnderline = generateReferenceTitleUnderline();
        posY += 3.0f;
        var refLabels = generateReferenceLabels();

        return new PdfBlock(refTitle, refUnderline, refLabels);
    }

    protected PdfElement generateReferenceTitle() {
        return PdfText.builder()
                .contentStream(cs)
                .text(requestMap.get(RequestMap.REF_TITLE))
                .dimensions(PdfDimensions.ofA4mm(150.0f, posY, 20.0f, 9.0f))
                .font(font)
                .colorRGB(TEMPLATE_COLOR)
                .build();
    }

    protected PdfElement generateReferenceTitleUnderline() {
        return PdfLine.builder()
                .contentStream(cs)
                .dimensions(PdfDimensions.ofA4mm(150.0f, posY, REFERENCE_UNDERLINE_WIDTH, 1.0f))
                .colorRGB(TEMPLATE_COLOR)
                .build();
    }

    protected PdfElement generateReferenceLabels() {
        var customerIdLabel = generateCustomerIdLabel();
        posY += 6.0f;
        var invoiceIdLabel = generateInvoiceIdLabel();
        posY += 6.0f;
        var invoiceDateLabel = generateInvoiceDateLabel();
        posY += 6.0f;

        return new PdfBlock(customerIdLabel, invoiceIdLabel, invoiceDateLabel);
    }

    protected PdfElement generateInvoiceIdLabel() {

        return PdfText.builder()
                .contentStream(cs)
                .text(requestMap.get(RequestMap.INVOICE_ID_LBL))
                .dimensions(PdfDimensions.ofA4mm(150.0f, posY, 30.0f, 12.0f))
                .colorRGB(TEMPLATE_COLOR)
                .build();
    }

    protected PdfElement generateCustomerIdLabel() {
        return PdfText.builder()
                .contentStream(cs)
                .text(requestMap.get(RequestMap.CUSTOMER_ID_LBL))
                .dimensions(PdfDimensions.ofA4mm(150.0f, posY, 30.0f, 12.0f))
                .colorRGB(TEMPLATE_COLOR)
                .build();
    }

    protected PdfElement generateInvoiceDateLabel() {
        return PdfText.builder()
                .contentStream(cs)
                .text(requestMap.get(RequestMap.INVOICE_DATE_LBL))
                .dimensions(PdfDimensions.ofA4mm(150.0f, posY, 30.0f, 12.0f))
                .colorRGB(TEMPLATE_COLOR)
                .build();
    }
}
