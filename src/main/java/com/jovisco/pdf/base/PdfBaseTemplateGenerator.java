package com.jovisco.pdf.base;

import com.jovisco.pdf.core.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;

public abstract class PdfBaseTemplateGenerator implements PdfDocumentGenerator {

    protected final static String HEADER_FILENAME = "jovisco-letter-head.png";
    protected final static float HEADER_WIDTH = 226.4f;
    protected final static float HEADER_HEIGHT = 80.0f;
    protected final static float ADDRESS_LINE_WIDTH = 201.0f;
    protected final static float ADDRESS_LINE_HEIGHT = 10.0f;
    protected final static String ADDRESS_LINE_FILENAME = "adresse_mini.jpg";
    protected final static float FOOTER_WIDTH = 481.1f;
    protected final static float FOOTER_HEIGHT = 70.0f;
    protected final static String FOOTER_FILENAME = "jovisco-letter-foot.png";

    protected final RequestMap requestMap;

    protected PDType0Font font;
    protected PDDocument template;
    protected PDPage page;
    protected PDPageContentStream cs;

    public PdfBaseTemplateGenerator(RequestMap requestMap) {
        this.requestMap = requestMap;
    }

    @Override
    public PDDocument generate(PDDocument template) {

        try {
            this.template = template;
            this.page = this.template.getPage(0);
            InputStream fontStream = PdfBaseTemplate.class.getResourceAsStream("/ArialMT.ttf");
            this.font = PDType0Font.load(template, fontStream);
            generateContent();
            return template;
        } catch (IOException | URISyntaxException e) {
            throw new PdfDocumentException(e.getMessage());
        }
    }

    @Override
    public RequestMap getRequestMap() {
        return this.requestMap;
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

    protected void generateContent() throws IOException, URISyntaxException {

        try (var cs = new PDPageContentStream(template, page)) {
            this.cs = cs;
            this.cs.setFont(font, 12 );
            var block = new PdfBlock(
                    generateHeaderImage(),
                    generateAddressLineImage(),
                    generateFooterImage()
            );
            block.print();
        }
    }

    protected PdfElement generateHeaderImage() throws IOException, URISyntaxException {
        return PdfImage.builder()
                .document(template)
                .contentStream(cs)
                .dimensions(PdfDimensions.ofA4mm(70.0f, 17.0f, HEADER_WIDTH, HEADER_HEIGHT))
                .imagePath(getImagePath(HEADER_FILENAME))
                .build();
    }

    protected PdfElement generateAddressLineImage() throws IOException, URISyntaxException {
        return PdfImage.builder()
                .document(template)
                .contentStream(cs)
                .dimensions(PdfDimensions.ofA4mm(25.0f, 54.0f,  ADDRESS_LINE_WIDTH, ADDRESS_LINE_HEIGHT))
                .imagePath(getImagePath(ADDRESS_LINE_FILENAME))
                .build();
    }

    protected PdfElement generateFooterImage() throws IOException, URISyntaxException {
        return PdfImage.builder()
                .document(template)
                .contentStream(cs)
                .dimensions(PdfDimensions.ofA4mm(25.0f, 282.0f, FOOTER_WIDTH, FOOTER_HEIGHT))
                .imagePath(getImagePath(FOOTER_FILENAME))
                .build();
    }

    private Path getImagePath(String imageFilename) throws IOException, URISyntaxException {
        var imageUrl = ClassLoader.getSystemResource(imageFilename);
        if (imageUrl == null) throw new IOException("Image file " + imageFilename + " not found");
        return Paths.get(imageUrl.toURI());
    }
}