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
import java.nio.file.Paths;
import java.util.Calendar;

public class InvoiceTemplateDEdeGenerator implements InvoicingPdfGenerator {

    private static final int[] TEMPLATE_COLOR = {1, 94, 104};
    private final static float LINE_WIDTH = 163.0f * PdfPageCoordsOnA4.PAGE_WIDTH_FACTOR;
    private final static float REFERENCE_UNDERLINE_WIDTH = 38.0f * PdfPageCoordsOnA4.PAGE_WIDTH_FACTOR;

    private final CreatePdfInvoiceTemplateRequest request;
    private final String baseTemplateFilePath;
    private final String targetFilePath;
    private final PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

    private PDDocument template;
    private PDPage page;
    private PDPageContentStream cs;

    public InvoiceTemplateDEdeGenerator(
            CreatePdfInvoiceTemplateRequest request,
            String baseTemplateFilePath,
            String targetFilePath)
    {
        this.request = request;
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

    private void fillMetaInformation() {

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

    private void generateContent() throws IOException {

        try (var cs = new PDPageContentStream(template, page, AppendMode.APPEND, false, true)) {
            this.cs = cs;
            generateInvoiceTitle();
            generateReferenceTitleAndLabel();
            generateItemsColumnsHeader();
        }
    }

    private void generateInvoiceTitle() throws IOException {
        InvoicingPdfText.builder()
                .contentStream(cs)
                .text(request.title())
                .dimensions(new PdfDimensions(26.0f, 92.0f, 40.0f, 24.0f))
                .font(font)
                .colorRGB(TEMPLATE_COLOR)
                .build()
                .printText();
    }

    private void generateReferenceTitleAndLabel() throws IOException {
        // print reference title
        printReferenceTitle();
        // draw underline for above text
        printReferenceTitleUnderline();
        // print reference labels
        generateReferenceLabels();
    }

    private void printReferenceTitle() throws IOException {
        InvoicingPdfText.builder()
                .contentStream(cs)
                .text(request.referenceTitle())
                .dimensions(new PdfDimensions(150.0f, 96.0f, 20.0f, 9.0f))
                .font(font)
                .build()
                .printText();
    }

    private void printReferenceTitleUnderline() throws IOException {
        InvoicingPdfLine.builder()
                .contentStream(cs)
                .dimensions(new PdfDimensions(150.0f, 98.0f, REFERENCE_UNDERLINE_WIDTH, 1.0f))
                .colorRGB(TEMPLATE_COLOR)
                .build()
                .draw();
    }

    private void generateReferenceLabels() throws IOException {
        printInvoiceIdLabel();
        printCustomerIdLabel();
        printInvoiceDateLabel();
    }

    private void printInvoiceIdLabel() throws IOException {
        InvoicingPdfText.builder()
                .contentStream(cs)
                .text("Re.Nr.")
                .dimensions(new PdfDimensions(150.0f, 107.0f, 30.0f, 12.0f))
                .colorRGB(TEMPLATE_COLOR)
                .build()
                .printText();
    }

    private void printCustomerIdLabel() throws IOException {
        InvoicingPdfText.builder()
                .contentStream(cs)
                .text("Kd.Nr.")
                .dimensions(new PdfDimensions(150.0f, 101.0f, 30.0f, 12.0f))
                .colorRGB(TEMPLATE_COLOR)
                .build()
                .printText();
    }

    private void printInvoiceDateLabel() throws IOException {
        InvoicingPdfText.builder()
                .contentStream(cs)
                .text("Datum")
                .dimensions(new PdfDimensions(150.0f, 113.0f, 30.0f, 12.0f))
                .colorRGB(TEMPLATE_COLOR)
                .build()
                .printText();
    }

    private void generateItemsColumnsHeader() throws IOException {
        drawLine(new PdfDimensions(26.0f, 124.0f, LINE_WIDTH, 1.0f));
        generateItemsColumnsHeaderTexts();
        drawLine(new PdfDimensions(26.0f, 130.0f, LINE_WIDTH, 1.0f));
    }

    private void drawLine(PdfDimensions dimensions) throws IOException {
        InvoicingPdfLine.builder()
                .contentStream(cs)
                .dimensions(dimensions)
                .build()
                .draw();
    }

    private void generateItemsColumnsHeaderTexts() throws IOException {
        InvoicingPdfText.builder()
                .contentStream(cs)
                .dimensions(new PdfDimensions(26.5f, 126.5f, LINE_WIDTH, 9.0f))
                .text(request.columnsHeader())
                .build()
                .printText();
    }


    @Override
    public boolean documentExists(String filePath) {
        return Paths.get(filePath).toFile().exists();
    }
}