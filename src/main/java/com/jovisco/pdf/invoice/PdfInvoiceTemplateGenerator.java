package com.jovisco.pdf.invoice;

import com.jovisco.pdf.core.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.IOException;

public abstract class PdfInvoiceTemplateGenerator implements PdfDocumentGenerator {

    protected static final int[] TEMPLATE_COLOR = {1, 94, 104};
    protected static final PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

    protected final RequestMap requestMap;

    protected PDDocument template;
    protected PDPage page;
    protected PDPageContentStream cs;
    protected PdfPosY posY;
    protected PdfBlockGenerator referenceHeaderBlockGenerator;
    protected PdfBlockGenerator itemsHeaderBlockGenerator;

    public PdfInvoiceTemplateGenerator(RequestMap requestMap) {
        this.requestMap = requestMap;
        this.posY = new PdfPosY();
    }

    public PDDocument generate(PDDocument template) {
        try {
            this.template = template;
            this.page = template.getPage(0);
            generateContent();
            return template;
        } catch (IOException e) {
            throw new PdfDocumentException(e.getMessage());
        }
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
        posY.setY(92.0f);
        return PdfText.builder()
                .contentStream(cs)
                .text(requestMap.get(RequestMap.TITLE))
                .dimensions(PdfDimensions.ofA4mm(26.0f, posY.getY(), 40.0f, 24.0f))
                .font(font)
                .colorRGB(TEMPLATE_COLOR)
                .build();
    }

    protected PdfElement generateReferenceTitleAndLabel() {
        posY.setY(96.0f);
        referenceHeaderBlockGenerator = new PdfInvoiceReferenceHeaderBlockGenerator(requestMap, cs, posY);
        return referenceHeaderBlockGenerator.generate();
    }

    protected PdfElement generateItemsHeaderBlock() {
        posY.setY(124.0f);
        itemsHeaderBlockGenerator = new PdfInvoiceItemsHeaderBlockGenerator(requestMap, cs, posY);
        return itemsHeaderBlockGenerator.generate();
    }
}