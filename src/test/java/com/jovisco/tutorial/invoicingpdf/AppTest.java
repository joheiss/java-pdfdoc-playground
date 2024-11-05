package com.jovisco.tutorial.invoicingpdf;


import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppTest {

    private final static float ADDRESS_Y_POS = 685.0f;
    private final static float ADDRESS_X_POS = 40.0f;
    private final static float TEXT_Y_POS_PERCENT = 60.0f/100.0f;
    private final static float TEXT_X_POS = 40.0f;
    private final static float HEADER_Y_POS_PERCENT = 90.0f/100.0f;
    private final static float HEADER_X_POS = 200.0f;
    private final static String HEADER_FILENAME = "jovisco-letter-head.png";
    private final static float HEADER_WIDTH = 226.4f;
    private final static float HEADER_HEIGHT = 80.0f;
    private final static float ADDRESS_LINE_X_POS = TEXT_X_POS;
    private final static float ADDRESS_LINE_Y_POS = 695.0f;
    private final static float ADDRESS_LINE_WIDTH = 201.0f;
    private final static float ADDRESS_LINE_HEIGHT = 10.0f;
    private final static String ADDRESS_LINE_FILENAME = "adresse_mini.jpg";
    private final static float TITLE_HEIGHT = 24.0f;
    private final static float FOOTER_Y_POS = 21.0f;
    private final static float FOOTER_X_POS = TEXT_X_POS;
    private final static String FOOTER_FILENAME = "jovisco-letter-foot.png";
    private final static float FOOTER_WIDTH = 481.1f;
    private final static float FOOTER_HEIGHT = 70.0f;
    private final static int[] TEMPLATE_COLOR = {1, 94, 104};
    private final static float LINE_WIDTH = 163.0f * PdfPageCoordsOnA4.PAGE_WIDTH_FACTOR;
    private final static float REFERENCE_UNDERLINE_WIDTH = 38.0f * PdfPageCoordsOnA4.PAGE_WIDTH_FACTOR;

    private final PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

    private static Map<String, PdfPageCoordsOnA4> elementPositions;
    private static CreatePdfRequest request;

    @BeforeAll
    static void init() {
        elementPositions = getElementPositions();
        request = makePdfRequest();
    }

    @Nested
    @DisplayName("General App Tests")
    class GeneralAppTests {
        @Test
        void shouldWork() {
            assertTrue(true);
        }
    }

    @Nested
    @DisplayName("Test Base Template")
    class BaseTemplateTests {

        @Test
        @DisplayName("should create the base template")
        void shouldCreateBaseTemplate() {
            var generator = new BaseTemplateDEdeGenerator("target/test-basetemplate.pdf");
            var template = generator.generate();
            assertNotNull(template);
        }
    }

    @Nested
    @DisplayName("Test Invoice Template")
    class InvoiceTemplateTests {

        @Test
        @DisplayName("should create the invoice template")
        void shouldCreateBInvoiceTemplate() throws URISyntaxException, IOException {
            var request = makePdfInvoiceTemplateRequest();
            var generator = new InvoiceTemplateDEdeGenerator(
                    request,
                    "target/test-basetemplate.pdf",
                    "target/test-invoicetemplate.pdf");
            var template = generator.generate();
            assertNotNull(template);
        }
    }

    @Nested
    @DisplayName("PdfBox Tests")
    class PdfBoxTests {
        @Test
        void shouldCreateASimplePDFDocumentInA4Format() {
            // get current date
            var now = Calendar.getInstance();
            Path headerImagePath;
            Path addressLineImagePath;
            Path footerImagePath;
            try {
                headerImagePath = Paths.get(ClassLoader.getSystemResource(HEADER_FILENAME).toURI());
                addressLineImagePath = Paths.get(ClassLoader.getSystemResource(ADDRESS_LINE_FILENAME).toURI());
                footerImagePath = Paths.get(ClassLoader.getSystemResource(FOOTER_FILENAME).toURI());
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
            try (var doc = new PDDocument()) {
                assertNotNull(doc);
                // prepare meta information
                var metadata = doc.getDocumentInformation();
                assertNotNull(metadata);
                metadata.setTitle("First Tests with PDFBox");
                metadata.setAuthor("Josef Heiss");
                metadata.setSubject("Tests with PDFBox");
                metadata.setCreationDate(now);
                metadata.setModificationDate(now);
                metadata.setKeywords("Java, PDFBox, Invoicing, Test");
                metadata.setProducer("PDFBox");
                // create and add page to document
                var page = new PDPage(PDRectangle.A4);
                assertNotNull(page);
                doc.addPage(page);
                // prepare initial text y position -> 15% from top
                final int initialTextYPosition = (int) (page.getBBox().getHeight() * TEXT_Y_POS_PERCENT);
                // prepare y position of logo
                final int headerYPosition = (int) (page.getBBox().getHeight() * HEADER_Y_POS_PERCENT);
                System.out.println("headerYPosition: " + headerYPosition);
                // prepare images
                var headerImage = PDImageXObject.createFromFile(headerImagePath.toAbsolutePath().toString(), doc);
                assertNotNull(headerImage);
                var addressLineImage = PDImageXObject.createFromFile(addressLineImagePath.toAbsolutePath().toString(), doc);
                assertNotNull(addressLineImage);
                var footerImage = PDImageXObject.createFromFile(footerImagePath.toAbsolutePath().toString(), doc);
                assertNotNull(footerImage);
                // prepare content
                try (var contentStream = new PDPageContentStream(doc, page)) {
                    assertNotNull(contentStream);
                    contentStream.setFont(font, 12);
                    contentStream.beginText();
                    contentStream.setLeading(15.0f);
                    contentStream.newLineAtOffset(ADDRESS_X_POS, ADDRESS_Y_POS);
                    for (var addressLine : request.addressLines()) {
                        contentStream.showText(addressLine);
                        contentStream.newLine();
                    }
                    contentStream.endText();
                    System.out.println("Start printing text at Y-position: " + initialTextYPosition);
                    contentStream.beginText();
                    contentStream.setLeading(15.0f);
                    contentStream.newLineAtOffset(TEXT_X_POS, initialTextYPosition);
                    contentStream.showText("Hello Josef");
                    contentStream.newLine();
                    contentStream.showText("We are really happy to see you struggling with Apache PDFBox.");
                    contentStream.newLine();
                    contentStream.showText("Can't you afford a more sophisticated library?");
                    contentStream.newLine();
                    contentStream.showText("Anyway, we hope you enjoy PDFBox!");
                    contentStream.endText();
                    contentStream.drawImage(headerImage, HEADER_X_POS, headerYPosition, HEADER_WIDTH, HEADER_HEIGHT);
                    contentStream.drawImage(addressLineImage, ADDRESS_LINE_X_POS, ADDRESS_LINE_Y_POS, ADDRESS_LINE_WIDTH, ADDRESS_LINE_HEIGHT);
                    contentStream.drawImage(footerImage, FOOTER_X_POS, FOOTER_Y_POS, FOOTER_WIDTH, FOOTER_HEIGHT);
                }
                // save document
                doc.save("target/test.pdf");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Test
        @DisplayName("Test Getting All the Sizes of a PDF Document")
        void testGettingAllTheSizesOfAPDFDocument() {

            try (var doc = new PDDocument()) {
                assertNotNull(doc);
                // create and add page to document
                var page = new PDPage(PDRectangle.A4);
                assertNotNull(page);
                doc.addPage(page);
                // look for page width and height
                var pageWidth = page.getBBox().getWidth();
                var pageHeight = page.getBBox().getHeight();
                System.out.println("pageWidth: " + pageWidth);
                System.out.println("pageHeight: " + pageHeight);
                // convert width & height to mm
                var pageWidthMM = pageWidth / 210.1f;
                var pageHeightMM = pageHeight / 297.10f;
                System.out.println("pageWidthUnitMM: " + pageWidthMM);
                System.out.println("pageHeightUnitMM: " + pageHeightMM);
                // bottom left & top right corner
                var lowerLeftX = page.getBBox().getLowerLeftX();
                var lowerLeftY = page.getBBox().getLowerLeftY();
                var upperRightX = page.getBBox().getUpperRightX();
                var upperRightY = page.getBBox().getUpperRightY();
                System.out.println("lowerLeftX: " + lowerLeftX);
                System.out.println("lowerLeftY: " + lowerLeftY);
                System.out.println("upperRightX: " + upperRightX);
                System.out.println("upperRightY: " + upperRightY);
                var pageBBox = page.getBBox().toString();
                System.out.println("pageBBox: " + pageBBox);

                float pageHeightInMM = 297.1f;
                float pageHeightConversionFactor = pageHeight / pageHeightInMM;
                float pageWidthUnitConversionFactor = pageWidth / pageWidthMM;
                // header coordinates
                float headerXFromLeftInMM = 70.0f;
                float headerYFromTopInMM = 17.0f;
//                float headerYFromBottomInMM = pageHeightInMM - headerYFromTopInMM;
//                float headerXPos = headerXFromLeftInMM * pageWidthUnitConversionFactor;
//                float headerYPos = headerYFromBottomInMM * pageHeightConversionFactor;
                var headerCoords = PdfPageCoordsOnA4.ofTopLeftInMM(headerXFromLeftInMM, headerYFromTopInMM);
                System.out.println("header coordinates: " + headerCoords.xPos() + ", " + headerCoords.yPos());
                // address line coordinates
                float addressLineXFromLeftInMM = 25;
                float addressLineYFromTopInMM = 54;
//                float addressLineYFromBottomInMM = pageHeightInMM - addressLineYFromTopInMM;
//                float addressLineXPos = addressLineXFromLeftInMM * pageWidthUnitConversionFactor;
//                float addressLineYPos = addressLineYFromBottomInMM * pageHeightConversionFactor;
                var addressLineCoords = PdfPageCoordsOnA4.ofTopLeftInMM(addressLineXFromLeftInMM, addressLineYFromTopInMM);
                System.out.println("address line coordinates: " + addressLineCoords.xPos() + ", " + addressLineCoords.yPos());
                // invoice title coordinates
                float invoiceTitleXFromLeftInMM = 26;
                float invoiceTitleYFromTopInMM = 92;
//                float invoiceTitleYFromBottomInMM = pageHeightInMM - invoiceTitleYFromTopInMM;
//                float invoiceTitleXPos = invoiceTitleXFromLeftInMM * pageWidthUnitConversionFactor;
//                float invoiceTitleYPos = invoiceTitleYFromBottomInMM * pageHeightConversionFactor;
                var invoiceTitleCoords = PdfPageCoordsOnA4.ofTopLeftInMM(invoiceTitleXFromLeftInMM, invoiceTitleYFromTopInMM);
                System.out.println("invoice title coordinates: " + invoiceTitleCoords.xPos() + ", " + invoiceTitleCoords.yPos());
                // reference coordinates
                float referenceXFromLeftInMM = 138;
                float referenceYFromTopInMM = 96;
//                float referenceYFromBottomInMM = pageHeightInMM - referenceYFromTopInMM;
//                float referenceXPos = referenceXFromLeftInMM * pageWidthUnitConversionFactor;
//                float referenceYPos = referenceYFromBottomInMM * pageHeightConversionFactor;
                var referenceCoords = PdfPageCoordsOnA4.ofTopLeftInMM(referenceXFromLeftInMM, referenceYFromTopInMM);
                System.out.println("reference coordinates: " + referenceCoords.xPos() + ", " + referenceCoords.yPos());
                // page number coordinates
                float pageNumberXFromLeftInMM = 138;
                float pageNumberYFromTopInMM = 119;
//                float pageNumberYFromBottomInMM = pageHeightInMM - pageNumberYFromTopInMM;
//                float pageNumberXPos = pageNumberXFromLeftInMM * pageWidthUnitConversionFactor;
//                float pageNumberYPos = pageNumberYFromBottomInMM * pageHeightConversionFactor;
                var pageNumberCoords = PdfPageCoordsOnA4.ofTopLeftInMM(pageNumberXFromLeftInMM, pageNumberYFromTopInMM);
                System.out.println("page number coordinates: " + pageNumberCoords.xPos() + ", " + pageNumberCoords.yPos());
                // billing period coordinates
                float billingPeriodXFromLeftInMM = 26;
                float billingPeriodYFromTopInMM = 112;
//                float billingPeriodYFromBottomInMM = pageHeightInMM - billingPeriodYFromTopInMM;
//                float billingPeriodXPos = billingPeriodXFromLeftInMM * pageWidthUnitConversionFactor;
//                float billingPeriodYPos = billingPeriodYFromBottomInMM * pageHeightConversionFactor;
                var billingPeriodCoords = PdfPageCoordsOnA4.ofTopLeftInMM(billingPeriodXFromLeftInMM, billingPeriodYFromTopInMM);
                System.out.println("billing period coordinates: " + billingPeriodCoords.xPos() + ", " + billingPeriodCoords.yPos());
                // items column header coordinates  - upper line
                float itemsColumnHeaderUpperLineXFromLeftInMM = 26;
                float itemsColumnHeaderUpperLineYFromTopInMM = 124;
//                float itemsColumnHeaderUpperLineYFromBottomInMM = pageHeightInMM - itemsColumnHeaderUpperLineYFromTopInMM;
//                float itemsColumnHeaderUpperLineXPos = itemsColumnHeaderUpperLineXFromLeftInMM * pageWidthUnitConversionFactor;
//                float itemsColumnHeaderUpperLineYPos = itemsColumnHeaderUpperLineYFromBottomInMM * pageHeightConversionFactor;
                float itemsColumnHeaderLineWidthInMM = 158;
                var itemsColumnHeaderUpperLineCoords = PdfPageCoordsOnA4.ofTopLeftInMM(itemsColumnHeaderUpperLineXFromLeftInMM, itemsColumnHeaderUpperLineYFromTopInMM);
                System.out.println("items column header upper line coordinates: " + itemsColumnHeaderUpperLineCoords.xPos() + ", " + itemsColumnHeaderUpperLineCoords.yPos());
                // items column header coordinates  - lower line
                float itemsColumnHeaderLowerLineXFromLeftInMM = 26;
                float itemsColumnHeaderLowerLineYFromTopInMM = 130;
//                float itemsColumnHeaderLowerLineYFromBottomInMM = pageHeightInMM - itemsColumnHeaderLowerLineYFromTopInMM;
//                float itemsColumnHeaderLowerLineXPos = itemsColumnHeaderLowerLineXFromLeftInMM * pageWidthUnitConversionFactor;
//                float itemsColumnHeaderLowerLineYPos = itemsColumnHeaderLowerLineYFromBottomInMM * pageHeightConversionFactor;
                var itemsColumnHeaderLowerLineCoords = PdfPageCoordsOnA4.ofTopLeftInMM(itemsColumnHeaderLowerLineXFromLeftInMM, itemsColumnHeaderLowerLineYFromTopInMM);
                System.out.println("items column header lower line coordinates: " + itemsColumnHeaderLowerLineCoords.xPos() + ", " + itemsColumnHeaderLowerLineCoords.yPos());
                // footer coordinates
                float footerXFromRightInMM = 25;
                float footerYFromTopInMM = 267;
//                float footerYFromBottomInMM = pageHeightInMM - footerYFromTopInMM;
//                float footerXPos = footerXFromRightInMM * pageWidthUnitConversionFactor;
//                float footerYPos = footerYFromBottomInMM * pageHeightConversionFactor;
                var footerCoords = PdfPageCoordsOnA4.ofTopLeftInMM(footerXFromRightInMM, footerYFromTopInMM);
                System.out.println("footer coordinates: " + footerCoords.xPos() + ", " + footerCoords.yPos());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Test
        void shouldPositionElementsInTheRightPlaces() {
            // get current date
            try (var template = new PDDocument()) {
                assertNotNull(template);
                // prepare meta information
                fillMetaInformation(template);
                // get images
                var images = getTemplateImages(template);
                // create and add page to document
                var page = new PDPage(PDRectangle.A4);
                assertNotNull(page);
                template.addPage(page);
                // prepare content for template
                try (var contentStream = new PDPageContentStream(template, page)) {
                    assertNotNull(contentStream);
                    // draw header image
                    var headerImage = images.get("header");
                    headerImage.draw(contentStream);
                    // draw address line image
                    var addressLineImage = images.get("addressLine");
                    addressLineImage.draw(contentStream);
                    // draw footer image
                    var footerImage = images.get("footer");
                    footerImage.draw(contentStream);
                }
                // save template
                template.save("target/basetemplate.pdf");
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }

        @Test
        @DisplayName("should read base template and prepare invoice template")
        void shouldReadTemplateAndPrepareInvoiceForm() throws IOException {

            try (var doc = Loader.loadPDF(new File("target/basetemplate.pdf"))) {

                assertNotNull(doc);
                // prepare meta information
                fillMetaInformation(doc);
                // get page
                var page = doc.getPage(0);
                assertNotNull(page);

                // add invoice specific content
                try (var cs = new PDPageContentStream(doc, page, AppendMode.APPEND, false, true)) {
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
                // save invoice template
                doc.save("target/invoicetemplate.pdf");
            }
        }
    }

    private static Map<String, PdfPageCoordsOnA4> getElementPositions() {

        Map<String, PdfPageCoordsOnA4> elementPositions = new HashMap<>();

        float headerXFromLeftInMM = 70.0f;
        float headerYFromTopInMM = 17.0f;
        var headerCoords = PdfPageCoordsOnA4.ofTopLeftWithHeightInMM(
                headerXFromLeftInMM, headerYFromTopInMM, HEADER_HEIGHT);
        elementPositions.put("header", headerCoords);

        float addressLineXFromLeftInMM = 25.0f;
        float addressLineYFromTopInMM = 54.0f;
        var addressLineCoords = PdfPageCoordsOnA4.ofTopLeftWithHeightInMM(
                addressLineXFromLeftInMM, addressLineYFromTopInMM, ADDRESS_LINE_HEIGHT);
        elementPositions.put("addressLine", addressLineCoords);

        float invoiceTitleXFromLeftInMM = 26.0f;
        float invoiceTitleYFromTopInMM = 92.0f;
        var invoiceTitleCoords = PdfPageCoordsOnA4.ofTopLeftWithHeightInMM(
                invoiceTitleXFromLeftInMM, invoiceTitleYFromTopInMM, TITLE_HEIGHT);
        elementPositions.put("invoiceTitle", invoiceTitleCoords);

        float referenceXFromLeftInMM = 138.0f;
        float referenceYFromTopInMM = 96.0f;
        var referenceCoords = PdfPageCoordsOnA4.ofTopLeftInMM(referenceXFromLeftInMM, referenceYFromTopInMM);
        elementPositions.put("reference", referenceCoords);

        float referenceUnderlineXFromLeftInMM = referenceXFromLeftInMM;
        float referenceUnderlineYFromTopInMM = 96.0f + 1.0f;
        var referenceUnderlineCoords = PdfPageCoordsOnA4.ofTopLeftInMM(
                referenceUnderlineXFromLeftInMM, referenceUnderlineYFromTopInMM);
        elementPositions.put("referenceUnderline", referenceUnderlineCoords);

        float pageNumberXFromLeftInMM = 138.0f;
        float pageNumberYFromTopInMM = 119.0f;
        var pageNumberCoords = PdfPageCoordsOnA4.ofTopLeftInMM(pageNumberXFromLeftInMM, pageNumberYFromTopInMM);
        elementPositions.put("pageNumber", pageNumberCoords);

        float billingPeriodXFromLeftInMM = 26.0f;
        float billingPeriodYFromTopInMM = 112.0f;
        var billingPeriodCoords = PdfPageCoordsOnA4.ofTopLeftInMM(billingPeriodXFromLeftInMM, billingPeriodYFromTopInMM);
        elementPositions.put("billingPeriod", billingPeriodCoords);

        float itemsColumnHeaderUpperLineXFromLeftInMM = 26.0f;
        float itemsColumnHeaderUpperLineYFromTopInMM = 124.0f;
        var itemsColumnHeaderUpperLineCoords = PdfPageCoordsOnA4.ofTopLeftInMM(
                itemsColumnHeaderUpperLineXFromLeftInMM,
                itemsColumnHeaderUpperLineYFromTopInMM);
        elementPositions.put("itemsColumnHeaderUpperLine", itemsColumnHeaderUpperLineCoords);

        float itemsColumnHeaderXFromLeftInMM = 26.5f;
        float itemsColumnHeaderYFromTopInMM = 128.0f;
        var itemsColumnHeaderCoords = PdfPageCoordsOnA4.ofTopLeftInMM(
                itemsColumnHeaderXFromLeftInMM,
                itemsColumnHeaderYFromTopInMM);
        elementPositions.put("itemsColumnHeader", itemsColumnHeaderCoords);

        float itemsColumnHeaderLowerLineXFromLeftInMM = 26.0f;
        float itemsColumnHeaderLowerLineYFromTopInMM = 130.0f;
        var itemsColumnHeaderLowerLineCoords = PdfPageCoordsOnA4.ofTopLeftInMM(
                itemsColumnHeaderLowerLineXFromLeftInMM,
                itemsColumnHeaderLowerLineYFromTopInMM);
        elementPositions.put("itemsColumnHeaderLowerLine", itemsColumnHeaderLowerLineCoords);

        float itemsTotalHeaderUpperLineXFromLeftInMM = 26.0f;
        float itemsTotalHeaderUpperLineYFromTopInMM = 136.0f;
        var itemsTotalHeaderUpperLineCoords = PdfPageCoordsOnA4.ofTopLeftInMM(
                itemsTotalHeaderUpperLineXFromLeftInMM,
                itemsTotalHeaderUpperLineYFromTopInMM);
        elementPositions.put("itemsTotalHeaderUpperLine", itemsTotalHeaderUpperLineCoords);

        float itemsTotalHeaderXFromLeftInMM = 26.5f;
        float itemsTotalHeaderYFromTopInMM = 140.0f;
        var itemsTotalHeaderCoords = PdfPageCoordsOnA4.ofTopLeftInMM(
                itemsTotalHeaderXFromLeftInMM,
                itemsTotalHeaderYFromTopInMM);
        elementPositions.put("itemsTotalHeader", itemsTotalHeaderCoords);

        float itemsTotalHeaderLowerLineXFromLeftInMM = 26.0f;
        float itemsTotalHeaderLowerLineYFromTopInMM = 142.0f;
        var itemsTotalHeaderLowerLineCoords = PdfPageCoordsOnA4.ofTopLeftInMM(
                itemsTotalHeaderLowerLineXFromLeftInMM,
                itemsTotalHeaderLowerLineYFromTopInMM);
        elementPositions.put("itemsTotalHeaderLowerLine", itemsTotalHeaderLowerLineCoords);

        float footerXFromLeftInMM = 25.0f;
        float footerYFromTopInMM = 282.0f;
        var footerCoords = PdfPageCoordsOnA4.ofTopLeftWithHeightInMM(
                footerXFromLeftInMM, footerYFromTopInMM, FOOTER_HEIGHT);
        elementPositions.put("footer", footerCoords);

        return elementPositions;
    }

    private static CreatePdfInvoiceTemplateRequest makePdfInvoiceTemplateRequest() {
        var textBuilder = new StringBuilder("Pos")
                .append("____")
                .append("Menge")
                .append("_____")
                .append("Beschreibung")
                .append("____________________________________")
                .append("Einzelpreis")
                .append("________")
                .append("Gesamtpreis");
        var columnsheader = textBuilder
                .toString()
                .replaceAll("_", "  ");
        textBuilder = new StringBuilder("_______")
                .append("Nettobetrag")
                .append("______________________")
                .append("19% MwSt")
                .append("___________________________________")
                .append("Bruttobetrag");
        var totalsHeader = textBuilder
                .toString()
                .replaceAll("_", "  ");
        return new CreatePdfInvoiceTemplateRequest(
                "Rechnung",
                "Bitte bei Zahlung angeben:",
                columnsheader,
                totalsHeader);
    }

    private static CreatePdfRequest makePdfRequest() {
        var textBuilder = new StringBuilder("Pos")
                .append("____")
                .append("Menge")
                .append("_____")
                .append("Beschreibung")
                .append("____________________________________")
                .append("Einzelpreis")
                .append("________")
                .append("Gesamtpreis");
        var columnsheader = textBuilder
                .toString()
                .replaceAll("_", "  ");
        textBuilder = new StringBuilder("_______")
                .append("Nettobetrag")
                .append("______________________")
                .append("19% MwSt")
                .append("___________________________________")
                .append("Bruttobetrag");
        var totalsHeader = textBuilder
                .toString()
                .replaceAll("_", "  ");
        return new CreatePdfRequest(
                "5222", "2.11.2024", "4711",
                "Rechnung",
                new String[]{"Jovisco AG", "Kapitalstr. 123", "", "12345 Berlin"},
                "Oktober 2024","Bitte bei Zahlung angeben:",
                columnsheader, totalsHeader,"12.345,67 €");
    }

    private void fillMetaInformation(PDDocument doc) {
        var now = Calendar.getInstance();

        var metadata = doc.getDocumentInformation();
        assertNotNull(metadata);
        metadata.setTitle("First Tests with PDFBox");
        metadata.setAuthor("Josef Heiss");
        metadata.setSubject("Tests with PDFBox");
        metadata.setCreationDate(now);
        metadata.setModificationDate(now);
        metadata.setKeywords("Java, PDFBox, Invoicing, Test");
        metadata.setProducer("PDFBox");
    }

    private Map<String, PDImageXObject> getImages(PDDocument doc) throws IOException {

        try {
            Map<String, PDImageXObject> images = new HashMap<>();
            var headerImagePath = Paths.get(ClassLoader.getSystemResource(HEADER_FILENAME).toURI());
            var headerImage = PDImageXObject.createFromFile(headerImagePath.toAbsolutePath().toString(), doc);
            images.put("header", headerImage);
            var addressLineImagePath = Paths.get(ClassLoader.getSystemResource(ADDRESS_LINE_FILENAME).toURI());
            var addressLineImage = PDImageXObject.createFromFile(addressLineImagePath.toAbsolutePath().toString(), doc);
            images.put("addressLine", addressLineImage);
            var footerImagePath = Paths.get(ClassLoader.getSystemResource(FOOTER_FILENAME).toURI());
            var footerImage = PDImageXObject.createFromFile(footerImagePath.toAbsolutePath().toString(), doc);
            images.put("footer", footerImage);
            return images;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    private Map<String, InvoicingPdfTemplateImage> getTemplateImages(PDDocument doc) throws IOException, URISyntaxException {

        Map<String, InvoicingPdfTemplateImage> images = new HashMap<>();

        var headerCoords = elementPositions.get("header");
        var headerImage = new InvoicingPdfTemplateImage(
                doc,
                Paths.get(ClassLoader.getSystemResource(HEADER_FILENAME).toURI()),
                new PdfDimensions(headerCoords.xPos(), headerCoords.yPos(), HEADER_WIDTH, HEADER_HEIGHT));
        images.put("header", headerImage);

        var addressLineCoords = elementPositions.get("addressLine");
        var addressLineImage = new InvoicingPdfTemplateImage(
                doc,
                Paths.get(ClassLoader.getSystemResource(ADDRESS_LINE_FILENAME).toURI()),
                new PdfDimensions(addressLineCoords.xPos(), addressLineCoords.yPos(), ADDRESS_LINE_WIDTH, ADDRESS_LINE_HEIGHT));
        images.put("addressLine", addressLineImage);

        var footerCoords = elementPositions.get("footer");
        var footerImage = new InvoicingPdfTemplateImage(
                doc,
                Paths.get(ClassLoader.getSystemResource(FOOTER_FILENAME).toURI()),
                new PdfDimensions(footerCoords.xPos(), footerCoords.yPos(), FOOTER_WIDTH, FOOTER_HEIGHT));
        images.put("footer", footerImage);

        return images;
    }

    private void drawLine(PDPageContentStream cs, float xPos, float yPos, float width) throws IOException {

        cs.moveTo(xPos, yPos);
        cs.lineTo(xPos + width, yPos);
        cs.stroke();
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
}