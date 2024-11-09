package com.jovisco.invoicing.pdf;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

public abstract class PdfInvoiceBlockGenerator {

    protected static final int[] TEXT_COLOR = {0, 0, 0};
    protected static final int[] TEMPLATE_COLOR = {1, 94, 104};
    protected final static float LINE_WIDTH = 163.0f * PdfDimensions.PAGE_WIDTH_FACTOR;
    protected final PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

    protected final RequestMap requestMap;
    protected final PDPageContentStream cs;
    protected final float startY;

    protected float posY;

    public PdfInvoiceBlockGenerator(RequestMap requestMap, PDPageContentStream cs, float startY) {
        this.requestMap = requestMap;
        this.cs = cs;
        this.startY = startY;
    }

    public abstract PdfElement generate();

    protected PdfElement generateLine(PdfDimensions dimensions) {
        return PdfLine.builder()
                .contentStream(cs)
                .dimensions(dimensions)
                .colorRGB(TEMPLATE_COLOR)
                .build();
    }

    protected float getPosY() {
        return posY;
    }
}
