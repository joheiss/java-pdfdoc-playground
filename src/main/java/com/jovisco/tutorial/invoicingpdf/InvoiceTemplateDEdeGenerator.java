package com.jovisco.tutorial.invoicingpdf;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Calendar;

public class InvoiceTemplateDEdeGenerator {

    private final CreatePdfInvoiceTemplateRequest request;
    private final String baseTemplateFilePath;
    private final String targetFilePath;
    private final PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

    private static final int[] TEMPLATE_COLOR = {1, 94, 104};
    private final static float LINE_WIDTH = 163.0f * PdfPageCoordsOnA4.PAGE_WIDTH_FACTOR;
    private final static float REFERENCE_UNDERLINE_WIDTH = 38.0f * PdfPageCoordsOnA4.PAGE_WIDTH_FACTOR;

    public InvoiceTemplateDEdeGenerator(
            CreatePdfInvoiceTemplateRequest request,
            String baseTemplateFilePath,
            String targetFilePath)
            throws URISyntaxException, IOException
    {
        this.request = request;
        this.baseTemplateFilePath = baseTemplateFilePath;
        this.targetFilePath = targetFilePath;
    }

    public PDDocument generate() {

        try (var template = Loader.loadPDF(new File(baseTemplateFilePath))) {
            // prepare meta information
            fillMetaInformation(template);
            // get page
            var page = template.getPage(0);

            // add invoice specific content
            try (var cs = new PDPageContentStream(template, page, AppendMode.APPEND, false, true)) {
                // print invoice title
                var dimensions = new PdfDimensions(26.0f, 92.0f, 40.0f, 24.0f);
                var invoiceTitle = InvoicingPdfText.builder()
                        .contentStream(cs)
                        .text(request.title())
                        .dimensions(dimensions)
                        .font(font)
                        .colorRGB(TEMPLATE_COLOR)
                        .build();
                cs.beginText();
                invoiceTitle.printText();
                cs.endText();
                // print reference header line
                dimensions = new PdfDimensions(138.0f, 96.0f, 20.0f, 9.0f);
                var referenceTitle = InvoicingPdfText.builder()
                        .contentStream(cs)
                        .text(request.referenceTitle())
                        .dimensions(dimensions)
                        .font(font)
                        .build();
                cs.beginText();
                referenceTitle.printText();
                cs.endText();
                // draw underline for above text
                dimensions = new PdfDimensions(138.0f, 98.0f, REFERENCE_UNDERLINE_WIDTH, 1.0f);
                var underline = InvoicingPdfLine.builder()
                        .contentStream(cs)
                        .dimensions(dimensions)
                        .colorRGB(TEMPLATE_COLOR)
                        .build();
                underline.draw();
                // column header
                printColumnHeader(cs);

                // print totals header
                printTotalsHeader(cs);
            }
            template.save(targetFilePath);
            return template;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void fillMetaInformation(PDDocument template) {

        var now = Calendar.getInstance();

        var metadata = template.getDocumentInformation();
        metadata.setTitle("Jovisco GmbH - Invoice Template (DE_de)");
        metadata.setAuthor("Jo Heiss");
        metadata.setSubject("Invoice Template");
        metadata.setCreationDate(now);
        metadata.setModificationDate(now);
        metadata.setKeywords("Jovisco, TemplateJava, PDFBox, Invoicing, DE_de");
        metadata.setProducer("PDFBox");
    }

    private void printColumnHeader(PDPageContentStream cs) throws IOException {

        // draw column header upper line
        var line = InvoicingPdfLine.builder()
                .contentStream(cs)
                .dimensions(new PdfDimensions(26.0f, 124.0f, LINE_WIDTH, 1.0f))
                .build();
        line.draw();
        // column headers
        var header = InvoicingPdfText.builder()
                .contentStream(cs)
                .dimensions(new PdfDimensions(26.5f, 126.5f, LINE_WIDTH, 9.0f))
                .text(request.columnsHeader())
                .build();
        cs.beginText();
        header.printText();
        cs.endText();
        // draw column header lower line
        line = InvoicingPdfLine.builder()
                .contentStream(cs)
                .dimensions(new PdfDimensions(26.0f, 130.0f, LINE_WIDTH, 1.0f))
                .build();
        line.draw();
    }

    private void printTotalsHeader(PDPageContentStream cs) throws IOException {

        // draw column header upper line
        var line = InvoicingPdfLine.builder()
                .contentStream(cs)
                .dimensions(new PdfDimensions(26.0f, 136.0f, LINE_WIDTH, 1.0f))
                .build();
        line.draw();
        // column headers
        var header = InvoicingPdfText.builder()
                .contentStream(cs)
                .dimensions(new PdfDimensions(26.5f, 138.5f, LINE_WIDTH, 9.0f))
                .text(request.totalsHeader())
                .build();
        cs.beginText();
        header.printText();
        cs.endText();
        // draw column header lower line
        line = InvoicingPdfLine.builder()
                .contentStream(cs)
                .dimensions(new PdfDimensions(26.0f, 142.0f, LINE_WIDTH, 1.0f))
                .build();
        line.draw();
    }

    public boolean templateExists(String filePath) {
        return Paths.get(filePath).toFile().exists();
    }
}