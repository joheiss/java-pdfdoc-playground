package com.jovisco.tutorial.invoicingpdf;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class InvoicingPdfTextBuilder {

    private PDPageContentStream contentStream;
    private String text;
    private PdfDimensions dimensions;
    private PDType1Font font;
    private int[] colorRGB;

    InvoicingPdfTextBuilder() {}

    InvoicingPdfText build() {
        return new InvoicingPdfText(contentStream, text, dimensions, font, colorRGB);
    }

    InvoicingPdfTextBuilder contentStream(PDPageContentStream contentStream) {
        this.contentStream = contentStream;
        return this;
    }

    InvoicingPdfTextBuilder text(String text) {
        this.text = text;
        return this;
    }

    InvoicingPdfTextBuilder dimensions(PdfDimensions dimensions) {
        this.dimensions = dimensions;
        return this;
    }

    InvoicingPdfTextBuilder font(PDType1Font font) {
        this.font = font;
        return this;
    }

    InvoicingPdfTextBuilder colorRGB(int[] colorRGB) {
        this.colorRGB = colorRGB;
        return this;
    }
}
