package com.jovisco.pdf.core;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

public abstract class PdfBlockGenerator {

    protected static final int[] TEXT_COLOR = {0, 0, 0};
    protected static final int[] TEMPLATE_COLOR = {1, 94, 104};
    protected static final float LINE_WIDTH = 166.0f * PdfDimensions.PAGE_WIDTH_FACTOR;

    protected final RequestMap requestMap;
    protected final PDPageContentStream cs;
    protected final PdfPosY posY;

    protected PDType0Font font;

    public PdfBlockGenerator(RequestMap requestMap, PDPageContentStream cs, PDType0Font font, PdfPosY posY) {
        this.requestMap = requestMap;
        this.cs = cs;
        this.font = font;
        this.posY = posY;
    }

    public abstract PdfElement generate();

    protected PdfElement generateLine(PdfDimensions dimensions) {
        return PdfLine.builder()
                .contentStream(cs)
                .dimensions(dimensions)
                .colorRGB(TEMPLATE_COLOR)
                .build();
    }
}
