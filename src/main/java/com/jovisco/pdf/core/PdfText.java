package com.jovisco.pdf.core;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.IOException;

public class PdfText implements PdfElement {

    private final PDPageContentStream cs;
    private final String text;
    private final PdfDimensions dimensions;
    private PDType0Font font = null;
    private float[] color = null;

    public static PdfTextBuilder builder() {
        return new PdfTextBuilder();
    }

    public PdfText(PDPageContentStream cs, String text, PdfDimensions dimensions, PDType0Font font, int[] color) {
        this(cs, text, dimensions, font);
        if (color != null) this.color = new float[]{color[0]/255.0f, color[1]/255.0f, color[2]/255.0f};
    }

    public PdfText(PDPageContentStream cs, String text, PdfDimensions dimensions, PDType0Font font) {
        this(cs, text, dimensions);
        this.font = font;
    }

    public PdfText(PDPageContentStream cs, String text, PdfDimensions dimensions) {
        this.cs = cs;
        this.text = text;
        this.dimensions = dimensions;
    }

    @Override
    public void print() throws IOException {
        cs.beginText();
        if (font != null) cs.setFont(font, dimensions.height());
        if (color != null) cs.setNonStrokingColor(this.color[0], this.color[1], this.color[2]);
        cs.newLineAtOffset(dimensions.x(), dimensions.y());
        cs.showText(text);
        cs.endText();
    }
}
