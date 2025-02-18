package com.jovisco.pdf.invoice;

import com.jovisco.pdf.base.PdfBaseTemplate;
import com.jovisco.pdf.core.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.IOException;
import java.io.InputStream;

public abstract class PdfInvoiceDocumentGenerator implements PdfDocumentGenerator {

    protected static final int[] TEXT_COLOR = {0, 0, 0};
    protected static final int[] TEMPLATE_COLOR = {1, 94, 104};

    protected final RequestMap requestMap;

    protected PDType0Font font;
    protected PDDocument doc;
    protected PDPage page;
    protected PDPageContentStream cs;
    protected String invoiceId;

    protected PdfPosY posY;
    protected PdfBlockGenerator referenceBlockGenerator;
    protected PdfBlockGenerator itemsBlockGenerator;
    protected PdfBlockGenerator itemsTotalsBlockGenerator;

    public PdfInvoiceDocumentGenerator(RequestMap requestMap) {
        this.requestMap = requestMap;
        this.invoiceId = this.requestMap.get(RequestMap.INVOICE_ID);
        this.posY = new PdfPosY();
    }

    public PDDocument generate(PDDocument document) {
        try {
            this.doc = document;
            this.page = document.getPage(0);
            InputStream fontStream = PdfBaseTemplate.class.getResourceAsStream("/ArialMT.ttf");
            this.font = PDType0Font.load(doc, fontStream);
            generateContent();
            return document;
        } catch (IOException e) {
            throw new PdfDocumentException(e.getMessage());
        }
    }

    @Override
    public RequestMap getRequestMap() {
        return this.requestMap;
    }

    protected void generateContent() throws IOException {

        try (var cs = new PDPageContentStream(doc, page, AppendMode.APPEND, false, true)) {
            this.cs = cs;
            this.cs.setFont( font, 10 );
            var blocks = new PdfBlock(
                    generateAddressLines(),
                    generateReferencesBlock(),
                    generateBillingPeriod(),
                    generateItemsBlock(),
                    generateTotalsBlock(),
                    generatePaymentTerm(),
                    generateOptionalInvoiceTexts()
            );
            blocks.print();
        }
    }

    protected PdfElement generateAddressLines() {
        posY.setY(58.0f);
        return PdfTextBlock.builder()
                .contentStream(cs)
                .textLines(requestMap.getList(RequestMap.ADDRESS_LINES))
                .font(font)
                .colorRGB(TEXT_COLOR)
                .dimensions(PdfDimensions.ofA4mm(26.0f, posY.getY(), 250.0f, 11.0f))
                .leading(15.0f)
                .build();
    }

    protected PdfElement generateReferencesBlock() {
        posY.setY(101.0f);
        referenceBlockGenerator = new PdfInvoiceReferenceBlockGenerator(requestMap, cs, font, posY);
        return referenceBlockGenerator.generate();
    }

    protected PdfElement generateBillingPeriod() {
        posY.setY(111.0f);
        return PdfText.builder()
                .contentStream(cs)
                .dimensions(PdfDimensions.ofA4mm(26.0f, posY.getY(), 250.0f, 11.0f))
                .text(requestMap.get(RequestMap.BILLING_PERIOD))
                .colorRGB(TEXT_COLOR)
                .build();
    }

    protected PdfElement generateItemsBlock() {
        posY.setY(135.0f);
        itemsBlockGenerator = new PdfInvoiceItemsBlockGenerator(requestMap, cs, font, posY);
        return itemsBlockGenerator.generate();
    }

    protected PdfElement generateTotalsBlock() {
        itemsTotalsBlockGenerator = new PdfInvoiceItemsTotalsBlockGenerator(requestMap, cs, font, posY);
        return itemsTotalsBlockGenerator.generate();
    }

    protected PdfElement generatePaymentTerm() {
        posY.incrementBy(10.0f);
        return PdfText.builder()
                .contentStream(cs)
                .dimensions(PdfDimensions.ofA4mm(26.5f, posY.getY(), 50.0f, 9.0f))
                .text(requestMap.get(RequestMap.PAYMENT_TERMS))
                .font(font)
                .colorRGB(TEMPLATE_COLOR)
                .build();
    }

    protected PdfElement generateOptionalInvoiceTexts() {
        posY.incrementBy(10.0f);
        return PdfTextBlock.builder()
                .contentStream(cs)
                .textLines(requestMap.getList(RequestMap.OPT_INVOICE_TEXTS))
                .font(font)
                .colorRGB(TEXT_COLOR)
                .dimensions(PdfDimensions.ofA4mm(26.0f, posY.getY(), 250.0f, 11.0f))
                .leading(15.0f)
                .build();
    }
}
