package com.jovisco.tutorial.invoicingpdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Calendar;

public class BaseTemplateDEdeGenerator implements InvoicingPdfGenerator {

    private final static String HEADER_FILENAME = "jovisco-letter-head.png";
    private final static float HEADER_WIDTH = 226.4f;
    private final static float HEADER_HEIGHT = 80.0f;
    private final static float ADDRESS_LINE_WIDTH = 201.0f;
    private final static float ADDRESS_LINE_HEIGHT = 10.0f;
    private final static String ADDRESS_LINE_FILENAME = "adresse_mini.jpg";
    private final static float FOOTER_WIDTH = 481.1f;
    private final static float FOOTER_HEIGHT = 70.0f;
    private final static String FOOTER_FILENAME = "jovisco-letter-foot.png";

    private final String filePath;
    private PDDocument template;
    private PDPage page;
    private PDPageContentStream cs;

    public BaseTemplateDEdeGenerator(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public PDDocument generate() {

        try (var template = new PDDocument()) {
            this.template = template;
            fillMetaInformation();
            var page = new PDPage(PDRectangle.A4);
            this.page = page;
            template.addPage(page);
            generateContent();
            template.save(filePath);
            return template;
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void generateContent() throws IOException, URISyntaxException {

        try (var cs = new PDPageContentStream(template, page)) {
            this.cs = cs;
            generateHeaderImage();
            generateAddressLineImage();
            generateFooterImage();
        }
    }

    private void generateHeaderImage() throws IOException, URISyntaxException {
        InvoicingPdfImage.builder()
                .document(template)
                .contentStream(cs)
                .dimensions(new PdfDimensions(70.0f, 17.0f, HEADER_WIDTH, HEADER_HEIGHT))
                .imagePath(Paths.get(ClassLoader.getSystemResource(HEADER_FILENAME).toURI()))
                .build()
                .draw();
    }
    
    private void generateAddressLineImage() throws IOException, URISyntaxException {
        InvoicingPdfImage.builder()
                .document(template)
                .contentStream(cs)
                .dimensions(new PdfDimensions(25.0f, 54.0f,  ADDRESS_LINE_WIDTH, ADDRESS_LINE_HEIGHT))
                .imagePath(Paths.get(ClassLoader.getSystemResource(ADDRESS_LINE_FILENAME).toURI()))
                .build()
                .draw();
    }

    private void generateFooterImage() throws IOException, URISyntaxException {
        InvoicingPdfImage.builder()
                .document(template)
                .contentStream(cs)
                .dimensions(new PdfDimensions(25.0f, 282.0f, FOOTER_WIDTH, FOOTER_HEIGHT))
                .imagePath(Paths.get(ClassLoader.getSystemResource(FOOTER_FILENAME).toURI()))
                .build()
                .draw();
    }

    private void fillMetaInformation() {

        var now = Calendar.getInstance();

        var metadata = template.getDocumentInformation();
        metadata.setTitle("Jovisco GmbH - Base Template");
        metadata.setAuthor("Jo Heiss");
        metadata.setSubject("Base Template");
        metadata.setCreationDate(now);
        metadata.setModificationDate(now);
        metadata.setKeywords("Jovisco, TemplateJava, PDFBox, Invoicing");
        metadata.setProducer("PDFBox");
    }

    @Override
    public boolean documentExists(String filePath) {
        return Paths.get(filePath).toFile().exists();
    }
}