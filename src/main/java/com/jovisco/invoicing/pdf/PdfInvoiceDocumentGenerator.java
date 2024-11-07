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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public abstract class PdfInvoiceDocumentGenerator implements PdfDocumentGenerator {

    protected static final int[] TEXT_COLOR = {0, 0, 0};
    protected static final int[] TEMPLATE_COLOR = {1, 94, 104};
    private final static float LINE_WIDTH = 163.0f * PdfDimensions.PAGE_WIDTH_FACTOR;

    protected final Map<String, List<Object>> requestMap;
    protected final String invoiceTemplateFilePath;
    protected final String targetFilePath;
    protected final PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

    protected PDDocument doc;
    protected PDPage page;
    protected PDPageContentStream cs;
    protected float posY;
    protected String invoiceId;

    public PdfInvoiceDocumentGenerator(
            Map<String, List<Object>> requestMap,
            String invoiceTemplateFilePath,
            String targetFilePath)
    {
        this.requestMap = requestMap;
        this.invoiceTemplateFilePath = invoiceTemplateFilePath;
        this.targetFilePath = targetFilePath;
        this.invoiceId = String.valueOf(requestMap.get(PdfInvoiceDocumentRequest.INVOICE_ID).getFirst());
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

        var addressLinesObj = requestMap.get(PdfInvoiceDocumentRequest.ADDRESS_LINES);
        List<String> addressLines = new ArrayList<>();
        addressLinesObj.forEach(obj -> addressLines.add(String.valueOf(obj)));

        PdfTextBlock.builder()
                .contentStream(cs)
                .textLines(addressLines)
                .font(font)
                .colorRGB(TEXT_COLOR)
                .dimensions(PdfDimensions.ofA4mm(26.0f, 58.0f, 250.0f, 12.0f))
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
        PdfText.builder()
                .contentStream(cs)
                .text(invoiceId)
                .dimensions(PdfDimensions.ofA4mm(167.0f, 107.0f, 30.0f, 12.0f))
                .colorRGB(TEXT_COLOR)
                .build()
                .printText();
    }

    protected void printCustomerId() throws IOException {
        PdfText.builder()
                .contentStream(cs)
                .text(String.valueOf(requestMap.get(PdfInvoiceDocumentRequest.CUSTOMER_ID).getFirst()))
                .dimensions(PdfDimensions.ofA4mm(167.0f, 101.0f, 30.0f, 12.0f))
                .colorRGB(TEXT_COLOR)
                .build()
                .printText();
    }

    protected void printInvoiceDate() throws IOException {
        PdfText.builder()
                .contentStream(cs)
                .text(String.valueOf(requestMap.get(PdfInvoiceDocumentRequest.INVOICE_DATE).getFirst()))
                .dimensions(PdfDimensions.ofA4mm(167.0f, 113.0f, 30.0f, 12.0f))
                .colorRGB(TEXT_COLOR)
                .build()
                .printText();
    }

    protected void generateBillingPeriod() throws IOException {
        PdfText.builder()
                .contentStream(cs)
                .dimensions(PdfDimensions.ofA4mm(26.0f, 111.0f, 250.0f, 12.0f))
                .text(String.valueOf(requestMap.get(PdfInvoiceDocumentRequest.BILLING_PERIOD).getFirst()))
                .colorRGB(TEXT_COLOR)
                .build()
                .printText();
    }

    protected void generateItemsBlock() throws IOException {

        // retrieve items from request map
        List<Map<String, String>> items = new ArrayList<>();
        var itemsObj = requestMap.get(PdfInvoiceDocumentRequest.ITEMS);
        itemsObj.forEach(obj -> items.add((Map<String, String>) obj));

        // ... and print the items
        posY = 135.0f;
        for (var item : items) {
            generateItem(item);
            posY += 7.0f;
        }
    }

    protected void generateItem(Map<String, String> itemMap) throws IOException {
        printItemId(itemMap.get(PdfInvoiceItemRequest.ITEM_ID));
        printItemQuantity(itemMap.get(PdfInvoiceItemRequest.ITEM_QTY));
        printItemDescription(itemMap.get(PdfInvoiceItemRequest.ITEM_DESC));
        printItemUnitNetAmount(itemMap.get(PdfInvoiceItemRequest.ITEM_UNIT_NET_AMNT));
        printItemTotalNetAmount(itemMap.get(PdfInvoiceItemRequest.ITEM_TOTAL_NET_AMNT));
    }

    protected void printItemId(String itemId) throws IOException {
        var itemX = 28.0f - (itemId.length() * 1.0f) + 1;
        PdfText.builder()
                .contentStream(cs)
                .font(font)
                .colorRGB(TEXT_COLOR)
                .text(itemId)
                .dimensions(PdfDimensions.ofA4mm(itemX, posY, 5.0f, 12.0f))
                .build()
                .printText();
    }

    protected void printItemQuantity(String quantity) throws IOException {
        var itemX = 47.0f - (quantity.length() * 2.0f) + 1;
        PdfText.builder()
                .contentStream(cs)
                .font(font)
                .colorRGB(TEXT_COLOR)
                .text(quantity)
                .dimensions(PdfDimensions.ofA4mm(itemX, posY, 10.0f, 12.0f))
                .build()
                .printText();
    }

    protected void printItemDescription(String description) throws IOException {
        var fontSize = 12.0f - (float) (description.length() / 35);
        PdfText.builder()
                .contentStream(cs)
                .font(font)
                .colorRGB(TEXT_COLOR)
                .text(description)
                .dimensions(PdfDimensions.ofA4mm(58.0f, posY, 80.0f, fontSize))
                .build()
                .printText();
    }

    protected void printItemUnitNetAmount(String amount) throws IOException {
        var itemX = 155.0f - (amount.length() * 2.1f) + 1;
        PdfText.builder()
                .contentStream(cs)
                .font(font)
                .colorRGB(TEXT_COLOR)
                .text(amount)
                .dimensions(PdfDimensions.ofA4mm(itemX, posY, 20.0f, 12.0f))
                .build()
                .printText();
    }

    protected void printItemTotalNetAmount(String amount) throws IOException {
        var itemX = 185.0f - (amount.length() * 1.8f) + 1;
        PdfText.builder()
                .contentStream(cs)
                .font(font)
                .colorRGB(TEXT_COLOR)
                .text(amount)
                .dimensions(PdfDimensions.ofA4mm(itemX, posY, 25.0f, 12.0f))
                .build()
                .printText();
    }

    protected void generateTotalsBlock() throws IOException {
        generateItemsTotalsHeader();
        posY += 5.0f;
        generateItemsTotalAmounts();
        posY += 5.0f;
        drawLine(PdfDimensions.ofA4mm(26.0f, posY, LINE_WIDTH, 1.0f));
    }

    protected void generateItemsTotalsHeader() throws IOException {
        drawLine(PdfDimensions.ofA4mm(26.0f, posY, LINE_WIDTH, 1.0f));
        posY += 2.5f;
        generateItemsTotalsHeaderTexts();
        posY += 3.0f;
        drawLine(PdfDimensions.ofA4mm(26.0f, posY, LINE_WIDTH, 1.0f));
    }

    protected void drawLine(PdfDimensions dimensions) throws IOException {
        PdfLine.builder()
                .contentStream(cs)
                .dimensions(dimensions)
                .colorRGB(TEMPLATE_COLOR)
                .build()
                .draw();
    }

    protected void generateItemsTotalsHeaderTexts() throws IOException {
        printTotalNetAmountHeader();
        printTotalVatAmountHeader();
        printTotalGrossAmountHeader();
    }

    private void printTotalNetAmountHeader() throws IOException {
        PdfText.builder()
                .contentStream(cs)
                .dimensions(PdfDimensions.ofA4mm(40.0f, posY, 20.0f, 9.0f))
                .text(String.valueOf(requestMap.get(PdfInvoiceDocumentRequest.TOTAL_NET_AMNT_HDR).getFirst()))
                .font(font)
                .colorRGB(TEMPLATE_COLOR)
                .build()
                .printText();
    }

    private void printTotalVatAmountHeader() throws IOException {
        PdfText.builder()
                .contentStream(cs)
                .dimensions(PdfDimensions.ofA4mm(96.5f, posY, 20.0f, 9.0f))
                .text(String.valueOf(requestMap.get(PdfInvoiceDocumentRequest.TOTAL_VAT_AMNT_HDR).getFirst()))
                .font(font)
                .colorRGB(TEMPLATE_COLOR)
                .build()
                .printText();
    }

    private void printTotalGrossAmountHeader() throws IOException {
        PdfText.builder()
                .contentStream(cs)
                .dimensions(PdfDimensions.ofA4mm(172.0f, posY, 20.0f, 9.0f))
                .text(String.valueOf(requestMap.get(PdfInvoiceDocumentRequest.TOTAL_GROSS_AMNT_HDR).getFirst()))
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
        var totalNetAmount = String.valueOf(requestMap.get(PdfInvoiceDocumentRequest.TOTAL_NET_AMNT).getFirst());
        var posX = 51.5f - (totalNetAmount.length() * 1.8f) + 1;
        PdfText.builder()
                .contentStream(cs)
                .font(font)
                .colorRGB(TEXT_COLOR)
                .text(totalNetAmount)
                .dimensions(PdfDimensions.ofA4mm(posX, posY, 25.0f, 12.0f))
                .build()
                .printText();
    }

    protected void printTotalVatAmount() throws IOException {
        var totalVatAmount = String.valueOf(requestMap.get(PdfInvoiceDocumentRequest.TOTAL_VAT_AMNT).getFirst());
        var posX = 108.0f - (totalVatAmount.length() * 1.8f) + 1;

        PdfText.builder()
                .contentStream(cs)
                .font(font)
                .colorRGB(TEXT_COLOR)
                .text(totalVatAmount)
                .dimensions(PdfDimensions.ofA4mm(posX, posY, 25.0f, 12.0f))
                .build()
                .printText();
    }

    protected void printTotalGrossAmount() throws IOException {
        var totalGrossAmount = String.valueOf(requestMap.get(PdfInvoiceDocumentRequest.TOTAL_GROSS_AMNT).getFirst());
        var posX = 185.0f - (totalGrossAmount.length() * 1.8f) + 1;
        PdfText.builder()
                .contentStream(cs)
                .font(font)
                .colorRGB(TEXT_COLOR)
                .text(totalGrossAmount)
                .dimensions(PdfDimensions.ofA4mm(posX, posY, 25.0f, 12.0f))
                .build()
                .printText();
    }
    protected void generatePaymentTerm() throws IOException {
        posY += 10.0f;
        PdfText.builder()
                .contentStream(cs)
                .dimensions(PdfDimensions.ofA4mm(26.5f, posY, 50.0f, 9.0f))
                .text(String.valueOf(requestMap.get(PdfInvoiceDocumentRequest.PAYMENT_TERMS).getFirst()))
                .font(font)
                .colorRGB(TEMPLATE_COLOR)
                .build()
                .printText();
    }

    protected void generateOptionalInvoiceTexts() throws IOException {

        var optionalInvoiceTextObj = requestMap.get(PdfInvoiceDocumentRequest.OPT_INVOICE_TEXTS);
        List<String> optionalInvoiceTexts = new ArrayList<>();
        optionalInvoiceTextObj.forEach(obj -> optionalInvoiceTexts.add(String.valueOf(obj)));

        posY += 10.0f;

        PdfTextBlock.builder()
                .contentStream(cs)
                .textLines(optionalInvoiceTexts)
                .font(new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE))
                .colorRGB(TEXT_COLOR)
                .dimensions(PdfDimensions.ofA4mm(26.0f, posY, 250.0f, 12.0f))
                .leading(15.0f)
                .build()
                .printTextBlock();
    }

    protected String generateTargetFilePath() {
        return "target/test-R" + invoiceId + ".pdf";
    }

    @Override
    public boolean documentExists(String filePath) {
        return false;
    }
}
