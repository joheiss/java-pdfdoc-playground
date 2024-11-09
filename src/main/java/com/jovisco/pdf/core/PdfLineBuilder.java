package com.jovisco.pdf.core;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

public class PdfLineBuilder {

    private PDPageContentStream contentStream;
    private PdfDimensions dimensions;
    private int[] colorRGB;

    PdfLineBuilder() {}

   public PdfLine build() {
        return new PdfLine(contentStream, dimensions, colorRGB);
    }

    public PdfLineBuilder contentStream(PDPageContentStream contentStream) {
        this.contentStream = contentStream;
        return this;
    }

    public PdfLineBuilder dimensions(PdfDimensions dimensions) {
        this.dimensions = dimensions;
        return this;
    }

    public PdfLineBuilder colorRGB(int[] colorRGB) {
        this.colorRGB = colorRGB;
        return this;
    }
}
