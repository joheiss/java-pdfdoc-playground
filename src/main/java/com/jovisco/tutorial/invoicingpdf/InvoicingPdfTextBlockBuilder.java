package com.jovisco.tutorial.invoicingpdf;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class InvoicingPdfTextBlockBuilder {

    private PDPageContentStream contentStream;
    private String[] textLines;
    private PdfDimensions dimensions;
    private PDType1Font font;
    private int[] colorRGB;
    private float leading;

    InvoicingPdfTextBlockBuilder() {}

    InvoicingPdfTextBlock build() {
        return new InvoicingPdfTextBlock(
                contentStream, textLines, dimensions, font, colorRGB, leading);
    }

    InvoicingPdfTextBlockBuilder contentStream(PDPageContentStream contentStream) {
        this.contentStream = contentStream;
        return this;
    }

    InvoicingPdfTextBlockBuilder textLines(String[] textLines) {
        this.textLines = textLines;
        return this;
    }

    InvoicingPdfTextBlockBuilder dimensions(PdfDimensions dimensions) {
        this.dimensions = dimensions;
        return this;
    }

    InvoicingPdfTextBlockBuilder font(PDType1Font font) {
        this.font = font;
        return this;
    }

    InvoicingPdfTextBlockBuilder colorRGB(int[] colorRGB) {
        this.colorRGB = colorRGB;
        return this;
    }

    InvoicingPdfTextBlockBuilder leading(float leading) {
        this.leading = leading;
        return this;
    }
}
