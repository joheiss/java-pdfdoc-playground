package com.jovisco.pdf.core;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

public abstract class PdfBlockGenerator {

    protected static final int[] TEXT_COLOR = {0, 0, 0};
    protected static final int[] TEMPLATE_COLOR = {1, 94, 104};
    protected static final float LINE_WIDTH = 163.0f * PdfDimensions.PAGE_WIDTH_FACTOR;
    protected static final PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

    protected final RequestMap requestMap;
    protected final PDPageContentStream cs;
    protected final PdfPosY posY;

    public PdfBlockGenerator(RequestMap requestMap, PDPageContentStream cs, PdfPosY posY) {
        this.requestMap = requestMap;
        this.cs = cs;
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
