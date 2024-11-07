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
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class PdfInvoiceTemplateGenerator implements PdfDocumentGenerator {

    protected static final int[] TEMPLATE_COLOR = {1, 94, 104};
    protected final static float LINE_WIDTH = 163.0f * PdfDimensions.PAGE_WIDTH_FACTOR;
    protected final static float REFERENCE_UNDERLINE_WIDTH = 38.0f * PdfDimensions.PAGE_WIDTH_FACTOR;

    protected final Map<String, List<String>> requestMap;
    protected final String baseTemplateFilePath;
    protected final String targetFilePath;
    protected final PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

    protected PDDocument template;
    protected PDPage page;
    protected PDPageContentStream cs;

    public PdfInvoiceTemplateGenerator(
            Map<String, List<String>> requestMap,
            String baseTemplateFilePath,
            String targetFilePath)
    {
        this.requestMap = requestMap;
        this.baseTemplateFilePath = baseTemplateFilePath;
        this.targetFilePath = targetFilePath;
    }

    @Override
    public PDDocument generate() {

        try (var template = Loader.loadPDF(new File(baseTemplateFilePath))) {
            this.template = template;
            fillMetaInformation();
            this.page = template.getPage(0);
            generateContent();
            template.save(targetFilePath);
            return template;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void fillMetaInformation() {

        var now = Calendar.getInstance();

        var metadata = template.getDocumentInformation();
        metadata.setTitle("Jovisco GmbH - Invoice Template (DE_de)");
        metadata.setAuthor("Jo Heiss");
        metadata.setSubject("Invoice Template");
        metadata.setCreationDate(now);
        metadata.setModificationDate(now);
        metadata.setKeywords("Jovisco, Template, Invoice, Invoicing, DE_de");
        metadata.setProducer("PDFBox");
    }

    protected void generateContent() throws IOException {

        try (var cs = new PDPageContentStream(template, page, AppendMode.APPEND, false, true)) {
            this.cs = cs;
            generateInvoiceTitle();
            generateReferenceTitleAndLabel();
            generateItemsColumnsHeader();
        }
    }

    protected void generateInvoiceTitle() throws IOException {
        PdfText.builder()
                .contentStream(cs)
                .text(requestMap.get(PdfInvoiceTemplateRequest.TITLE).getFirst())
                .dimensions(PdfDimensions.ofA4mm(26.0f, 92.0f, 40.0f, 24.0f))
                .font(font)
                .colorRGB(TEMPLATE_COLOR)
                .build()
                .printText();
    }

    protected void generateReferenceTitleAndLabel() throws IOException {
        printReferenceTitle();
        printReferenceTitleUnderline();
        generateReferenceLabels();
    }

    protected void printReferenceTitle() throws IOException {
        PdfText.builder()
                .contentStream(cs)
                .text(requestMap.get(PdfInvoiceTemplateRequest.REF_TITLE).getFirst())
                .dimensions(PdfDimensions.ofA4mm(150.0f, 96.0f, 20.0f, 9.0f))
                .font(font)
                .build()
                .printText();
    }

    protected void printReferenceTitleUnderline() throws IOException {
        PdfLine.builder()
                .contentStream(cs)
                .dimensions(PdfDimensions.ofA4mm(150.0f, 98.0f, REFERENCE_UNDERLINE_WIDTH, 1.0f))
                .colorRGB(TEMPLATE_COLOR)
                .build()
                .draw();
    }

    protected void generateReferenceLabels() throws IOException {
        printInvoiceIdLabel();
        printCustomerIdLabel();
        printInvoiceDateLabel();
    }

    protected void printInvoiceIdLabel() throws IOException {
        PdfText.builder()
                .contentStream(cs)
                .text(requestMap.get(PdfInvoiceTemplateRequest.INVOICE_ID_LBL).getFirst())
                .dimensions(PdfDimensions.ofA4mm(150.0f, 107.0f, 30.0f, 12.0f))
                .colorRGB(TEMPLATE_COLOR)
                .build()
                .printText();
    }

    protected void printCustomerIdLabel() throws IOException {
        PdfText.builder()
                .contentStream(cs)
                .text(requestMap.get(PdfInvoiceTemplateRequest.CUSTOMER_ID_LBL).getFirst())
                .dimensions(PdfDimensions.ofA4mm(150.0f, 101.0f, 30.0f, 12.0f))
                .colorRGB(TEMPLATE_COLOR)
                .build()
                .printText();
    }

    protected void printInvoiceDateLabel() throws IOException {
        PdfText.builder()
                .contentStream(cs)
                .text(requestMap.get(PdfInvoiceTemplateRequest.INVOICE_DATE_LBL).getFirst())
                .dimensions(PdfDimensions.ofA4mm(150.0f, 113.0f, 30.0f, 12.0f))
                .colorRGB(TEMPLATE_COLOR)
                .build()
                .printText();
    }

    protected void generateItemsColumnsHeader() throws IOException {
        drawLine(PdfDimensions.ofA4mm(26.0f, 124.0f, LINE_WIDTH, 1.0f));
        generateItemsColumnsHeaderTexts();
        drawLine(PdfDimensions.ofA4mm(26.0f, 130.0f, LINE_WIDTH, 1.0f));
    }

    protected void drawLine(PdfDimensions dimensions) throws IOException {
        PdfLine.builder()
                .contentStream(cs)
                .dimensions(dimensions)
                .build()
                .draw();
    }

    protected void generateItemsColumnsHeaderTexts() throws IOException {
        printItemIdHeader();
        printItemQuantityHeader();
        printItemDescriptionHeader();
        printItemUnitNetAmountHeader();
        printItemTotalNetAmountHeader();
    }

    private void printItemIdHeader() throws IOException {
        PdfText.builder()
                .contentStream(cs)
                .dimensions(PdfDimensions.ofA4mm(26.5f, 126.5f, 10.0f, 9.0f))
                .text(requestMap.get(PdfInvoiceTemplateRequest.ITEM_ID_HDR).getFirst())
                .build()
                .printText();
    }

    private void printItemQuantityHeader() throws IOException {
        PdfText.builder()
                .contentStream(cs)
                .dimensions(PdfDimensions.ofA4mm(38.5f, 126.5f, 10.0f, 9.0f))
                .text(requestMap.get(PdfInvoiceTemplateRequest.ITEM_QTY_HDR).getFirst())
                .build()
                .printText();
    }

    private void printItemDescriptionHeader() throws IOException {
        PdfText.builder()
                .contentStream(cs)
                .dimensions(PdfDimensions.ofA4mm(58.0f, 126.5f, 10.0f, 9.0f))
                .text(requestMap.get(PdfInvoiceTemplateRequest.ITEM_DESC_HDR).getFirst())
                .build()
                .printText();
    }

    private void printItemUnitNetAmountHeader() throws IOException {
        PdfText.builder()
                .contentStream(cs)
                .dimensions(PdfDimensions.ofA4mm(141.0f, 126.5f, 10.0f, 9.0f))
                .text(requestMap.get(PdfInvoiceTemplateRequest.ITEM_UNIT_NET_AMNT_HDR).getFirst())
                .build()
                .printText();
    }

    private void printItemTotalNetAmountHeader() throws IOException {
        PdfText.builder()
                .contentStream(cs)
                .dimensions(PdfDimensions.ofA4mm(171.0f, 126.5f, 10.0f, 9.0f))
                .text(requestMap.get(PdfInvoiceTemplateRequest.ITEM_TOTAL_NET_AMNT_HDR).getFirst())
                .build()
                .printText();
    }


    @Override
    public boolean documentExists(String filePath) {
        return Paths.get(filePath).toFile().exists();
    }
}