package com.jovisco.invoicing.pdf;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

public class PdfLineBuilder {

    private PDPageContentStream contentStream;
    private PdfDimensions dimensions;
    private int[] colorRGB;

    PdfLineBuilder() {}

   PdfLine build() {
        return new PdfLine(contentStream, dimensions, colorRGB);
    }

    PdfLineBuilder contentStream(PDPageContentStream contentStream) {
        this.contentStream = contentStream;
        return this;
    }

    PdfLineBuilder dimensions(PdfDimensions dimensions) {
        this.dimensions = dimensions;
        return this;
    }

    PdfLineBuilder colorRGB(int[] colorRGB) {
        this.colorRGB = colorRGB;
        return this;
    }
}
