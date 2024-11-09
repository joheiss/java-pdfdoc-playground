package com.jovisco.invoicing.pdf;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public abstract class PdfInvoiceDocumentGenerator implements PdfDocumentGenerator {

    protected static final int[] TEXT_COLOR = {0, 0, 0};
    protected static final int[] TEMPLATE_COLOR = {1, 94, 104};

    protected final RequestMap requestMap;
    protected final String invoiceTemplateFilePath;
    protected final String targetFilePath;
    protected final PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

    protected PDDocument doc;
    protected PDPage page;
    protected PDPageContentStream cs;
    protected float posY;
    protected String invoiceId;

    protected PdfInvoiceReferenceBlockGenerator referenceBlockGenerator;
    protected PdfInvoiceItemsBlockGenerator itemsBlockGenerator;
    protected PdfInvoiceItemsTotalsBlockGenerator itemsTotalsBlockGenerator;


    public PdfInvoiceDocumentGenerator(
            RequestMap requestMap,
            String invoiceTemplateFilePath,
            String targetFilePath)
    {
        this.requestMap = requestMap;
        this.invoiceTemplateFilePath = invoiceTemplateFilePath;
        this.targetFilePath = targetFilePath;
        this.invoiceId = this.requestMap.get(RequestMap.INVOICE_ID);
    }

    @Override
    public PDDocument generate() {
        try (var doc = Loader.loadPDF(new File(invoiceTemplateFilePath))) {
            this.doc = doc;
            fillMetaInformation();
            this.page = doc.getPage(0);
            generateContent();
            doc.save(generateTargetFilePath());
            return doc;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void fillMetaInformation() {

        var now = Calendar.getInstance();

        var metadata = doc.getDocumentInformation();
        metadata.setTitle("Jovisco GmbH - Invoice " + invoiceId);
        metadata.setAuthor("Jo Heiss");
        metadata.setSubject("Invoice " + invoiceId);
        metadata.setCreationDate(now);
        metadata.setModificationDate(now);
        metadata.setKeywords("Jovisco, Invoice, " + invoiceId);
        metadata.setProducer("PDFBox");
    }

    protected void generateContent() throws IOException {

        try (var cs = new PDPageContentStream(doc, page, AppendMode.APPEND, false, true)) {
            this.cs = cs;
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
        return PdfTextBlock.builder()
                .contentStream(cs)
                .textLines(requestMap.getList(RequestMap.ADDRESS_LINES))
                .font(font)
                .colorRGB(TEXT_COLOR)
                .dimensions(PdfDimensions.ofA4mm(26.0f, 58.0f, 250.0f, 12.0f))
                .leading(15.0f)
                .build();
    }

    protected PdfElement generateReferencesBlock() {
        posY = 101.0f;
        referenceBlockGenerator = new PdfInvoiceReferenceBlockGenerator(requestMap, cs, posY);
        var references = referenceBlockGenerator.generate();
        posY = referenceBlockGenerator.getPosY();
        return references;
    }

    protected PdfElement generateBillingPeriod() {
        return PdfText.builder()
                .contentStream(cs)
                .dimensions(PdfDimensions.ofA4mm(26.0f, 111.0f, 250.0f, 12.0f))
                .text(requestMap.get(RequestMap.BILLING_PERIOD))
                .colorRGB(TEXT_COLOR)
                .build();
    }

    protected PdfElement generateItemsBlock() {
        posY = 135.0f;
        itemsBlockGenerator = new PdfInvoiceItemsBlockGenerator(requestMap, cs, posY);
        var items = itemsBlockGenerator.generate();
        posY = itemsBlockGenerator.getPosY();
        return items;
    }

    protected PdfElement generateTotalsBlock() {
        itemsTotalsBlockGenerator = new PdfInvoiceItemsTotalsBlockGenerator(requestMap, cs, posY);
        var totals = itemsTotalsBlockGenerator.generate();
        posY = itemsTotalsBlockGenerator.getPosY();
        return totals;
    }

    protected PdfElement generatePaymentTerm() {
        posY += 10.0f;
        return PdfText.builder()
                .contentStream(cs)
                .dimensions(PdfDimensions.ofA4mm(26.5f, posY, 50.0f, 9.0f))
                .text(requestMap.get(RequestMap.PAYMENT_TERMS))
                .font(font)
                .colorRGB(TEMPLATE_COLOR)
                .build();
    }

    protected PdfElement generateOptionalInvoiceTexts() {
        posY += 10.0f;
        return PdfTextBlock.builder()
                .contentStream(cs)
                .textLines(requestMap.getList(RequestMap.OPT_INVOICE_TEXTS))
                .font(new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE))
                .colorRGB(TEXT_COLOR)
                .dimensions(PdfDimensions.ofA4mm(26.0f, posY, 250.0f, 12.0f))
                .leading(15.0f)
                .build();
    }

    protected String generateTargetFilePath() {
        return "target/test-R" + invoiceId + ".pdf";
    }
}
