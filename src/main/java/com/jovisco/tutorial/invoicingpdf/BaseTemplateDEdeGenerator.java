package com.jovisco.tutorial.invoicingpdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Calendar;

public class BaseTemplateDEdeGenerator {

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

    public BaseTemplateDEdeGenerator(String filePath) {
        this.filePath = filePath;
    }

    public PDDocument generate() {

        try (var template = new PDDocument()) {
            // prepare meta information
            fillMetaInformation(template);

            // create and add page to document
            var page = new PDPage(PDRectangle.A4);
            template.addPage(page);

            // prepare content for template
            try (var cs = new PDPageContentStream(template, page)) {
                // draw header image
                var headerImage = new InvoicingPdfTemplateImage(
                        template,
                        Paths.get(ClassLoader.getSystemResource(HEADER_FILENAME).toURI()),
                        new PdfDimensions(70.0f, 17.0f, HEADER_WIDTH, HEADER_HEIGHT));
                headerImage.draw(cs);
                // draw address line image
                var addressLineImage = new InvoicingPdfTemplateImage(template,
                        Paths.get(ClassLoader.getSystemResource(ADDRESS_LINE_FILENAME).toURI()),
                        new PdfDimensions(25.0f, 54.0f, ADDRESS_LINE_WIDTH, ADDRESS_LINE_HEIGHT));
                addressLineImage.draw(cs);
                // draw footer image
                var footerImage = new InvoicingPdfTemplateImage(
                        template,
                        Paths.get(ClassLoader.getSystemResource(FOOTER_FILENAME).toURI()),
                        new PdfDimensions(25.0f, 282.0f, FOOTER_WIDTH, FOOTER_HEIGHT));
                footerImage.draw(cs);
            }
            template.save(filePath);
            return template;
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void fillMetaInformation(PDDocument template) {

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

    public boolean templateExists(String filePath) {
        return Paths.get(filePath).toFile().exists();
    }
}