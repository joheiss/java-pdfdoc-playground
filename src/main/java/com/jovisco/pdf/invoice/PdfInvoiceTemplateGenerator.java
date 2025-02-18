package com.jovisco.pdf.invoice;

import com.jovisco.pdf.base.PdfBaseTemplate;
import com.jovisco.pdf.core.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.IOException;
import java.io.InputStream;

public abstract class PdfInvoiceTemplateGenerator implements PdfDocumentGenerator {

    protected static final int[] TEMPLATE_COLOR = {1, 94, 104};

    protected final RequestMap requestMap;

    protected PDType0Font font;
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
            InputStream fontStream = PdfBaseTemplate.class.getResourceAsStream("/ArialMT.ttf");
            this.font = PDType0Font.load(template, fontStream);
            generateContent();
            return template;
        } catch (IOException e) {
            throw new PdfDocumentException(e.getMessage());
        }
    }

    protected void generateContent() throws IOException {

        try (var cs = new PDPageContentStream(template, page, AppendMode.APPEND, false, true)) {
            this.cs = cs;
            this.cs.setFont( font, 12 );
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
                .dimensions(PdfDimensions.ofA4mm(26.0f, posY.getY(), 40.0f, 20.0f))
                .font(font)
                .colorRGB(TEMPLATE_COLOR)
                .build();
    }

    protected PdfElement generateReferenceTitleAndLabel() {
        posY.setY(96.0f);
        referenceHeaderBlockGenerator = new PdfInvoiceReferenceHeaderBlockGenerator(requestMap, cs, font, posY);
        return referenceHeaderBlockGenerator.generate();
    }

    protected PdfElement generateItemsHeaderBlock() {
        posY.setY(124.0f);
        itemsHeaderBlockGenerator = new PdfInvoiceItemsHeaderBlockGenerator(requestMap, cs, font, posY);
        return itemsHeaderBlockGenerator.generate();
    }
}