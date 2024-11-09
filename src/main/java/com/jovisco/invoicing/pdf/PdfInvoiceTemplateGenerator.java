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
import java.util.Calendar;

public abstract class PdfInvoiceTemplateGenerator implements PdfDocumentGenerator {

    protected static final int[] TEMPLATE_COLOR = {1, 94, 104};

    protected final RequestMap requestMap;
    protected final String baseTemplateFilePath;
    protected final String targetFilePath;
    protected final PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

    protected PDDocument template;
    protected PDPage page;
    protected PDPageContentStream cs;
    protected PdfInvoiceReferenceHeaderBlockGenerator referenceHeaderBlockGenerator;
    protected PdfInvoiceItemsHeaderBlockGenerator itemsHeaderBlockGenerator;
    protected float posY;

    public PdfInvoiceTemplateGenerator(
            RequestMap requestMap,
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
            var blocks = new PdfBlock(
                generateInvoiceTitle(),
                generateReferenceTitleAndLabel(),
                generateItemsHeaderBlock()
            );
            blocks.print();
        }
    }

    protected PdfElement generateInvoiceTitle() {
        return PdfText.builder()
                .contentStream(cs)
                .text(requestMap.get(RequestMap.TITLE))
                .dimensions(PdfDimensions.ofA4mm(26.0f, 92.0f, 40.0f, 24.0f))
                .font(font)
                .colorRGB(TEMPLATE_COLOR)
                .build();
    }

    protected PdfElement generateReferenceTitleAndLabel() {
        posY = 96.0f;
        referenceHeaderBlockGenerator = new PdfInvoiceReferenceHeaderBlockGenerator(requestMap, cs, posY);
        var refHeader = referenceHeaderBlockGenerator.generate();
        posY = referenceHeaderBlockGenerator.getPosY();
        return refHeader;
    }

    protected PdfElement generateItemsHeaderBlock() {
        posY = 124.0f;
        itemsHeaderBlockGenerator = new PdfInvoiceItemsHeaderBlockGenerator(requestMap, cs, posY);
        var itemsHeader = itemsHeaderBlockGenerator.generate();
        posY = itemsHeaderBlockGenerator.getPosY();
        return itemsHeader;
    }
}