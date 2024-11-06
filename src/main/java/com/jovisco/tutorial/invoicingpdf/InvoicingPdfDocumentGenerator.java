package com.jovisco.tutorial.invoicingpdf;

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

public abstract class InvoicingPdfDocumentGenerator implements InvoicingPdfGenerator {

    protected static final int[] TEXT_COLOR = {0, 0, 0};
    protected static final int[] TEMPLATE_COLOR = {1, 94, 104};
    private final static float LINE_WIDTH = 163.0f * PdfPageCoordsOnA4.PAGE_WIDTH_FACTOR;

    protected final CreatePdfInvoiceDocumentRequest request;
    protected final String invoiceTemplateFilePath;
    protected final String targetFilePath;
    protected final PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

    protected PDDocument doc;
    protected PDPage page;
    protected PDPageContentStream cs;
    protected float posY;

    public InvoicingPdfDocumentGenerator(
            CreatePdfInvoiceDocumentRequest request,
            String invoiceTemplateFilePath,
            String targetFilePath)
    {
        this.request = request;
        this.invoiceTemplateFilePath = invoiceTemplateFilePath;
        this.targetFilePath = targetFilePath;
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
        metadata.setTitle("Jovisco GmbH - Invoice " + request.invoiceId());
        metadata.setAuthor("Jo Heiss");
        metadata.setSubject("Invoice " + request.invoiceId());
        metadata.setCreationDate(now);
        metadata.setModificationDate(now);
        metadata.setKeywords("Jovisco, Invoice, " + request.invoiceId());
        metadata.setProducer("PDFBox");
    }

    protected void generateContent() throws IOException {

        try (var cs = new PDPageContentStream(doc, page, AppendMode.APPEND, false, true)) {
            this.cs = cs;
            generateAddressLines();
            generateReferencesBlock();
            generateBillingPeriod();
            generateItemsBlock();
            generateTotalsBlock();
            generatePaymentTerm();
            generateOptionalInvoiceTexts();
        }
    }

    protected void generateAddressLines() throws IOException {
        InvoicingPdfTextBlock.builder()
                .contentStream(cs)
                .textLines(request.addressLines())
                .font(font)
                .colorRGB(TEXT_COLOR)
                .dimensions(new PdfDimensions(26.0f, 58.0f, 250.0f, 12.0f))
                .leading(15.0f)
                .build()
                .printTextBlock();
    }

    protected void generateReferencesBlock() throws IOException {
        printInvoiceId();
        printCustomerId();
        printInvoiceDate();
    }

    protected void printInvoiceId() throws IOException {
        InvoicingPdfText.builder()
                .contentStream(cs)
                .text(request.invoiceId())
                .dimensions(new PdfDimensions(167.0f, 107.0f, 30.0f, 12.0f))
                .colorRGB(TEXT_COLOR)
                .build()
                .printText();
    }

    protected void printCustomerId() throws IOException {
        InvoicingPdfText.builder()
                .contentStream(cs)
                .text(request.customerId())
                .dimensions(new PdfDimensions(167.0f, 101.0f, 30.0f, 12.0f))
                .colorRGB(TEXT_COLOR)
                .build()
                .printText();
    }

    protected void printInvoiceDate() throws IOException {
        InvoicingPdfText.builder()
                .contentStream(cs)
                .text(request.formattedInvoiceDate())
                .dimensions(new PdfDimensions(167.0f, 113.0f, 30.0f, 12.0f))
                .colorRGB(TEXT_COLOR)
                .build()
                .printText();
    }

    protected void generateBillingPeriod() throws IOException {
        InvoicingPdfText.builder()
                .contentStream(cs)
                .dimensions(new PdfDimensions(26.0f, 111.0f, 250.0f, 12.0f))
                .text(request.billingPeriod())
                .colorRGB(TEXT_COLOR)
                .build()
                .printText();
    }

    protected void generateItemsBlock() throws IOException {
        posY = 135.0f;
        for (var item : request.items()) {
            generateItem(item);
            posY += 7.0f;
        }
    }

    protected void generateItem(CreatePdfInvoiceItemRequest item) throws IOException {
        printItemId(item.itemId());
        printItemQuantity(item.formattedQuantity());
        printItemDescription(item.description());
        printItemUnitNetAmount(item.formattedUnitNetAmount());
        printItemTotalNetAmount(item.formattedTotalNetAmount());
    }

    protected void printItemId(String itemId) throws IOException {
        var itemX = 28.0f - (itemId.length() * 1.0f) + 1;
        InvoicingPdfText.builder()
                .contentStream(cs)
                .font(font)
                .colorRGB(TEXT_COLOR)
                .text(itemId)
                .dimensions(new PdfDimensions(itemX, posY, 5.0f, 12.0f))
                .build()
                .printText();
    }

    protected void printItemQuantity(String quantity) throws IOException {
        var itemX = 47.0f - (quantity.length() * 2.0f) + 1;
        InvoicingPdfText.builder()
                .contentStream(cs)
                .font(font)
                .colorRGB(TEXT_COLOR)
                .text(quantity)
                .dimensions(new PdfDimensions(itemX, posY, 10.0f, 12.0f))
                .build()
                .printText();
    }

    protected void printItemDescription(String description) throws IOException {
        var fontSize = 12.0f - (float) (description.length() / 35);
        InvoicingPdfText.builder()
                .contentStream(cs)
                .font(font)
                .colorRGB(TEXT_COLOR)
                .text(description)
                .dimensions(new PdfDimensions(58.0f, posY, 80.0f, fontSize))
                .build()
                .printText();
    }

    protected void printItemUnitNetAmount(String amount) throws IOException {
        var itemX = 155.0f - (amount.length() * 2.1f) + 1;
        InvoicingPdfText.builder()
                .contentStream(cs)
                .font(font)
                .colorRGB(TEXT_COLOR)
                .text(amount)
                .dimensions(new PdfDimensions(itemX, posY, 20.0f, 12.0f))
                .build()
                .printText();
    }

    protected void printItemTotalNetAmount(String amount) throws IOException {
        var itemX = 185.0f - (amount.length() * 1.8f) + 1;
        InvoicingPdfText.builder()
                .contentStream(cs)
                .font(font)
                .colorRGB(TEXT_COLOR)
                .text(amount)
                .dimensions(new PdfDimensions(itemX, posY, 25.0f, 12.0f))
                .build()
                .printText();
    }

    protected void generateTotalsBlock() throws IOException {
        generateItemsTotalsHeader();
        posY += 5.0f;
        generateItemsTotalAmounts();
        posY += 5.0f;
        drawLine(new PdfDimensions(26.0f, posY, LINE_WIDTH, 1.0f));
    }

    protected void generateItemsTotalsHeader() throws IOException {
        drawLine(new PdfDimensions(26.0f, posY, LINE_WIDTH, 1.0f));
        posY += 2.5f;
        generateItemsTotalsHeaderTexts();
        posY += 3.0f;
        drawLine(new PdfDimensions(26.0f, posY, LINE_WIDTH, 1.0f));
    }

    protected void drawLine(PdfDimensions dimensions) throws IOException {
        InvoicingPdfLine.builder()
                .contentStream(cs)
                .dimensions(dimensions)
                .colorRGB(TEMPLATE_COLOR)
                .build()
                .draw();
    }

    protected void generateItemsTotalsHeaderTexts() throws IOException {
        InvoicingPdfText.builder()
                .contentStream(cs)
                .dimensions(new PdfDimensions(26.5f, posY, LINE_WIDTH, 9.0f))
                .text(request.totalsHeader())
                .font(font)
                .colorRGB(TEMPLATE_COLOR)
                .build()
                .printText();
    }

    protected void generateItemsTotalAmounts() throws IOException {
        printTotalNetAmount();
        printTotalVatAmount();
        printTotalGrossAmount();
    }

    protected void printTotalNetAmount() throws IOException {
        var posX = 51.5f - (request.formattedTotalGrossAmount().length() * 1.8f) + 1;
        InvoicingPdfText.builder()
                .contentStream(cs)
                .font(font)
                .colorRGB(TEXT_COLOR)
                .text(request.formattedTotalNetAmount())
                .dimensions(new PdfDimensions(posX, posY, 25.0f, 12.0f))
                .build()
                .printText();
    }

    protected void printTotalVatAmount() throws IOException {
        var posX = 108.0f - (request.formattedTotalGrossAmount().length() * 1.8f) + 1;
        InvoicingPdfText.builder()
                .contentStream(cs)
                .font(font)
                .colorRGB(TEXT_COLOR)
                .text(request.formattedTotalVatAmount())
                .dimensions(new PdfDimensions(posX, posY, 25.0f, 12.0f))
                .build()
                .printText();
    }

    protected void printTotalGrossAmount() throws IOException {
        var posX = 185.0f - (request.formattedTotalGrossAmount().length() * 1.8f) + 1;
        InvoicingPdfText.builder()
                .contentStream(cs)
                .font(font)
                .colorRGB(TEXT_COLOR)
                .text(request.formattedTotalGrossAmount())
                .dimensions(new PdfDimensions(posX, posY, 25.0f, 12.0f))
                .build()
                .printText();
    }
    protected void generatePaymentTerm() throws IOException {
        posY += 10.0f;
        InvoicingPdfText.builder()
                .contentStream(cs)
                .dimensions(new PdfDimensions(26.5f, posY, 50.0f, 9.0f))
                .text(request.paymentTerms())
                .font(font)
                .colorRGB(TEMPLATE_COLOR)
                .build()
                .printText();
    }

    protected void generateOptionalInvoiceTexts() throws IOException {
        posY += 10.0f;
        InvoicingPdfTextBlock.builder()
                .contentStream(cs)
                .textLines(request.optionalInvoiceTexts())
                .font(new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE))
                .colorRGB(TEXT_COLOR)
                .dimensions(new PdfDimensions(26.0f, posY, 250.0f, 12.0f))
                .leading(15.0f)
                .build()
                .printTextBlock();
    }

    protected String generateTargetFilePath() {
        return "target/test-R" + request.invoiceId() + ".pdf";
    }

    @Override
    public boolean documentExists(String filePath) {
        return false;
    }
}
