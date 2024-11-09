package com.jovisco.pdf.core;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.util.List;

public class PdfTextBlockBuilder {

    private PDPageContentStream contentStream;
    private List<String> textLines;
    private PdfDimensions dimensions;
    private PDType1Font font;
    private int[] colorRGB;
    private float leading;

    PdfTextBlockBuilder() {}

    public PdfTextBlock build() {
        return new PdfTextBlock(
                contentStream, textLines, dimensions, font, colorRGB, leading);
    }

    public PdfTextBlockBuilder contentStream(PDPageContentStream contentStream) {
        this.contentStream = contentStream;
        return this;
    }

    public PdfTextBlockBuilder textLines(List<String> textLines) {
        this.textLines = textLines;
        return this;
    }

    public PdfTextBlockBuilder dimensions(PdfDimensions dimensions) {
        this.dimensions = dimensions;
        return this;
    }

    public PdfTextBlockBuilder font(PDType1Font font) {
        this.font = font;
        return this;
    }

    public PdfTextBlockBuilder colorRGB(int[] colorRGB) {
        this.colorRGB = colorRGB;
        return this;
    }

    public PdfTextBlockBuilder leading(float leading) {
        this.leading = leading;
        return this;
    }
}
