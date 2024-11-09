package com.jovisco.pdf.core;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class PdfTextBuilder {

    private PDPageContentStream contentStream;
    private String text;
    private PdfDimensions dimensions;
    private PDType1Font font;
    private int[] colorRGB;

    PdfTextBuilder() {}

    public PdfText build() {
        return new PdfText(contentStream, text, dimensions, font, colorRGB);
    }

    public PdfTextBuilder contentStream(PDPageContentStream contentStream) {
        this.contentStream = contentStream;
        return this;
    }

    public PdfTextBuilder text(String text) {
        this.text = text;
        return this;
    }

    public PdfTextBuilder dimensions(PdfDimensions dimensions) {
        this.dimensions = dimensions;
        return this;
    }

    public PdfTextBuilder font(PDType1Font font) {
        this.font = font;
        return this;
    }

    public PdfTextBuilder colorRGB(int[] colorRGB) {
        this.colorRGB = colorRGB;
        return this;
    }
}
