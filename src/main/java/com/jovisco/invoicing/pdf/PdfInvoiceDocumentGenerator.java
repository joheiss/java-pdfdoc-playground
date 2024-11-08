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
    private final static float LINE_WIDTH = 163.0f * PdfDimensions.PAGE_WIDTH_FACTOR;

    protected final RequestMap requestMap;
    protected final String invoiceTemplateFilePath;
    protected final String targetFilePath;
    protected final PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

    protected PDDocument doc;
    protected PDPage page;
    protected PDPageContentStream cs;
    protected float posY;
    protected String invoiceId;
    protected PdfInvoiceItemsBlockGenerator itemsBlockGenerator;

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
        return new PdfBlock(
                generateInvoiceId(),
                generateCustomerId(),
                generateInvoiceDate()
        );
    }

    protected PdfElement generateInvoiceId() {
        return PdfText.builder()
                .contentStream(cs)
                .text(invoiceId)
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
        var itemsTotalsHeader = generateItemsTotalsHeader();
        posY += 5.0f;
        var itemTotalsAmounts = generateItemsTotalAmounts();
        posY += 5.0f;
        var line = drawLine(PdfDimensions.ofA4mm(26.0f, posY, LINE_WIDTH, 1.0f));

        return new PdfBlock(itemsTotalsHeader, itemTotalsAmounts, line);
    }

    protected PdfElement generateItemsTotalsHeader() {
        var topLine = drawLine(PdfDimensions.ofA4mm(26.0f, posY, LINE_WIDTH, 1.0f));
        posY += 2.5f;
        var itemsTotalsHeaderTexts = generateItemsTotalsHeaderTexts();
        posY += 3.0f;
        var bottomLine = drawLine(PdfDimensions.ofA4mm(26.0f, posY, LINE_WIDTH, 1.0f));

        return new PdfBlock(topLine, itemsTotalsHeaderTexts, bottomLine);
    }

    protected PdfElement drawLine(PdfDimensions dimensions) {
        return PdfLine.builder()
                .contentStream(cs)
                .dimensions(dimensions)
                .colorRGB(TEMPLATE_COLOR)
                .build();
    }

    protected PdfElement generateItemsTotalsHeaderTexts() {
        return new PdfBlock(
                generateTotalNetAmountHeader(),
                generateTotalVatAmountHeader(),
                generateTotalGrossAmountHeader()
        );
    }

    private PdfElement generateTotalNetAmountHeader() {
        return PdfText.builder()
                .contentStream(cs)
                .dimensions(PdfDimensions.ofA4mm(40.0f, posY, 20.0f, 9.0f))
                .text(requestMap.get(RequestMap.TOTAL_NET_AMNT_HDR))
                .font(font)
                .colorRGB(TEMPLATE_COLOR)
                .build();
    }

    private PdfElement generateTotalVatAmountHeader() {
        return PdfText.builder()
                .contentStream(cs)
                .dimensions(PdfDimensions.ofA4mm(96.5f, posY, 20.0f, 9.0f))
                .text(requestMap.get(RequestMap.TOTAL_VAT_AMNT_HDR))
                .font(font)
                .colorRGB(TEMPLATE_COLOR)
                .build();
    }

    private PdfElement generateTotalGrossAmountHeader() {
        return PdfText.builder()
                .contentStream(cs)
                .dimensions(PdfDimensions.ofA4mm(172.0f, posY, 20.0f, 9.0f))
                .text(requestMap.get(RequestMap.TOTAL_GROSS_AMNT_HDR))
                .font(font)
                .colorRGB(TEMPLATE_COLOR)
                .build();
    }

    protected PdfElement generateItemsTotalAmounts() {
        return new PdfBlock(
                generateTotalNetAmount(),
                generateTotalVatAmount(),
                generateTotalGrossAmount()
        );

    }

    protected PdfElement generateTotalNetAmount() {
        var totalNetAmount = requestMap.get(RequestMap.TOTAL_NET_AMNT);
        var posX = 51.5f - (totalNetAmount.length() * 1.8f) + 1;

        return PdfText.builder()
                .contentStream(cs)
                .font(font)
                .colorRGB(TEXT_COLOR)
                .text(totalNetAmount)
                .dimensions(PdfDimensions.ofA4mm(posX, posY, 25.0f, 12.0f))
                .build();
    }

    protected PdfElement generateTotalVatAmount() {
        var totalVatAmount = requestMap.get(RequestMap.TOTAL_VAT_AMNT);
        var posX = 108.0f - (totalVatAmount.length() * 1.8f) + 1;

        return PdfText.builder()
                .contentStream(cs)
                .font(font)
                .colorRGB(TEXT_COLOR)
                .text(totalVatAmount)
                .dimensions(PdfDimensions.ofA4mm(posX, posY, 25.0f, 12.0f))
                .build();
    }

    protected PdfElement generateTotalGrossAmount() {
        var totalGrossAmount = requestMap.get(RequestMap.TOTAL_GROSS_AMNT);
        var posX = 185.0f - (totalGrossAmount.length() * 1.8f) + 1;

        return PdfText.builder()
                .contentStream(cs)
                .font(font)
                .colorRGB(TEXT_COLOR)
                .text(totalGrossAmount)
                .dimensions(PdfDimensions.ofA4mm(posX, posY, 25.0f, 12.0f))
                .build();
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

    @Override
    public boolean documentExists(String filePath) {
        return false;
    }
}
