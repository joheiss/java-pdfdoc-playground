package com.jovisco.invoicing.pdf;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class PdfTextBuilder {

    private PDPageContentStream contentStream;
    private String text;
    private PdfDimensions dimensions;
    private PDType1Font font;
    private int[] colorRGB;

    PdfTextBuilder() {}

    PdfText build() {
        return new PdfText(contentStream, text, dimensions, font, colorRGB);
    }

    PdfTextBuilder contentStream(PDPageContentStream contentStream) {
        this.contentStream = contentStream;
        return this;
    }

    PdfTextBuilder text(String text) {
        this.text = text;
        return this;
    }

    PdfTextBuilder dimensions(PdfDimensions dimensions) {
        this.dimensions = dimensions;
        return this;
    }

    PdfTextBuilder font(PDType1Font font) {
        this.font = font;
        return this;
    }

    PdfTextBuilder colorRGB(int[] colorRGB) {
        this.colorRGB = colorRGB;
        return this;
    }
}
