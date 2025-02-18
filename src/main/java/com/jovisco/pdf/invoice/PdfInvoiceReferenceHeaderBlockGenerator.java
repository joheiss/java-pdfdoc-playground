package com.jovisco.pdf.invoice;

import com.jovisco.pdf.core.*;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

public class PdfInvoiceReferenceHeaderBlockGenerator extends PdfBlockGenerator {

    protected final static float REFERENCE_UNDERLINE_WIDTH = 44.0f * PdfDimensions.PAGE_WIDTH_FACTOR;

    public PdfInvoiceReferenceHeaderBlockGenerator(RequestMap requestMap, PDPageContentStream cs, PDType0Font font, PdfPosY posY) {
        super(requestMap, cs, font, posY);
    }

    @Override
    public PdfElement generate() {
        var refTitle = generateReferenceTitle();
        posY.incrementBy(2.0f);
        var refUnderline = generateReferenceTitleUnderline();
        posY.incrementBy(3.0f);
        var refLabels = generateReferenceLabels();

        return new PdfBlock(refTitle, refUnderline, refLabels);
    }

    protected PdfElement generateReferenceTitle() {
        return PdfText.builder()
                .contentStream(cs)
                .text(requestMap.get(RequestMap.REF_TITLE))
                .dimensions(PdfDimensions.ofA4mm(150.0f, posY.getY(), 20.0f, 9.0f))
                .font(font)
                .colorRGB(TEMPLATE_COLOR)
                .build();
    }

    protected PdfElement generateReferenceTitleUnderline() {
        return PdfLine.builder()
                .contentStream(cs)
                .dimensions(PdfDimensions.ofA4mm(150.0f, posY.getY(), REFERENCE_UNDERLINE_WIDTH, 1.0f))
                .colorRGB(TEMPLATE_COLOR)
                .build();
    }

    protected PdfElement generateReferenceLabels() {
        var customerIdLabel = generateCustomerIdLabel();
        posY.incrementBy(6.0f);
        var invoiceIdLabel = generateInvoiceIdLabel();
        posY.incrementBy(6.0f);
        var invoiceDateLabel = generateInvoiceDateLabel();
        posY.incrementBy(6.0f);

        return new PdfBlock(customerIdLabel, invoiceIdLabel, invoiceDateLabel);
    }

    protected PdfElement generateInvoiceIdLabel() {

        return PdfText.builder()
                .contentStream(cs)
                .text(requestMap.get(RequestMap.INVOICE_ID_LBL))
                .dimensions(PdfDimensions.ofA4mm(150.0f, posY.getY(), 30.0f, 12.0f))
                .font(font)
                .colorRGB(TEMPLATE_COLOR)
                .build();
    }

    protected PdfElement generateCustomerIdLabel() {
        return PdfText.builder()
                .contentStream(cs)
                .text(requestMap.get(RequestMap.CUSTOMER_ID_LBL))
                .dimensions(PdfDimensions.ofA4mm(150.0f, posY.getY(), 30.0f, 12.0f))
                .font(font)
                .colorRGB(TEMPLATE_COLOR)
                .build();
    }

    protected PdfElement generateInvoiceDateLabel() {
        return PdfText.builder()
                .contentStream(cs)
                .text(requestMap.get(RequestMap.INVOICE_DATE_LBL))
                .dimensions(PdfDimensions.ofA4mm(150.0f, posY.getY(), 30.0f, 12.0f))
                .font(font)
                .colorRGB(TEMPLATE_COLOR)
                .build();
    }
}
