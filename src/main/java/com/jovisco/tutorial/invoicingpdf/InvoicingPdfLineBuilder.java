package com.jovisco.tutorial.invoicingpdf;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

public class InvoicingPdfLineBuilder {

    private PDPageContentStream contentStream;
    private PdfDimensions dimensions;
    private int[] colorRGB;

    InvoicingPdfLineBuilder() {}

   InvoicingPdfLine build() {
        return new InvoicingPdfLine(contentStream, dimensions, colorRGB);
    }

    InvoicingPdfLineBuilder contentStream(PDPageContentStream contentStream) {
        this.contentStream = contentStream;
        return this;
    }

    InvoicingPdfLineBuilder dimensions(PdfDimensions dimensions) {
        this.dimensions = dimensions;
        return this;
    }

    InvoicingPdfLineBuilder colorRGB(int[] colorRGB) {
        this.colorRGB = colorRGB;
        return this;
    }
}
