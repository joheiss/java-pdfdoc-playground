package com.jovisco.invoicing.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Calendar;

public class PdfBaseTemplateGenerator implements PdfDocumentGenerator {

    protected final static String HEADER_FILENAME = "jovisco-letter-head.png";
    protected final static float HEADER_WIDTH = 226.4f;
    protected final static float HEADER_HEIGHT = 80.0f;
    protected final static float ADDRESS_LINE_WIDTH = 201.0f;
    protected final static float ADDRESS_LINE_HEIGHT = 10.0f;
    protected final static String ADDRESS_LINE_FILENAME = "adresse_mini.jpg";
    protected final static float FOOTER_WIDTH = 481.1f;
    protected final static float FOOTER_HEIGHT = 70.0f;
    protected final static String FOOTER_FILENAME = "jovisco-letter-foot.png";

    protected final String filePath;
    protected PDDocument template;
    protected PDPage page;
    protected PDPageContentStream cs;

    public PdfBaseTemplateGenerator(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public PDDocument generate() {

        try (var template = new PDDocument()) {
            this.template = template;
            fillMetaInformation();
            this.page = new PDPage(PDRectangle.A4);
            template.addPage(page);
            generateContent();
            template.save(filePath);
            return template;
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    protected void generateContent() throws IOException, URISyntaxException {

        try (var cs = new PDPageContentStream(template, page)) {
            this.cs = cs;
            generateHeaderImage();
            generateAddressLineImage();
            generateFooterImage();
        }
    }

    protected void generateHeaderImage() throws IOException, URISyntaxException {
        PdfImage.builder()
                .document(template)
                .contentStream(cs)
                .dimensions(PdfDimensions.ofA4mm(70.0f, 17.0f, HEADER_WIDTH, HEADER_HEIGHT))
                .imagePath(Paths.get(ClassLoader.getSystemResource(HEADER_FILENAME).toURI()))
                .build()
                .draw();
    }

    protected void generateAddressLineImage() throws IOException, URISyntaxException {
        PdfImage.builder()
                .document(template)
                .contentStream(cs)
                .dimensions(PdfDimensions.ofA4mm(25.0f, 54.0f,  ADDRESS_LINE_WIDTH, ADDRESS_LINE_HEIGHT))
                .imagePath(Paths.get(ClassLoader.getSystemResource(ADDRESS_LINE_FILENAME).toURI()))
                .build()
                .draw();
    }

    protected void generateFooterImage() throws IOException, URISyntaxException {
        PdfImage.builder()
                .document(template)
                .contentStream(cs)
                .dimensions(PdfDimensions.ofA4mm(25.0f, 282.0f, FOOTER_WIDTH, FOOTER_HEIGHT))
                .imagePath(Paths.get(ClassLoader.getSystemResource(FOOTER_FILENAME).toURI()))
                .build()
                .draw();
    }

    protected void fillMetaInformation() {

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