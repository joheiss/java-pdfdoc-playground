package com.jovisco.tutorial.invoicingpdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.io.IOException;
import java.nio.file.Path;

public class InvoicingPdfImageBuilder {

    private PDDocument document;
    private PDPageContentStream contentStream;
    private Path imagePath;
    private PdfDimensions dimensions;

    InvoicingPdfImageBuilder() {}

   InvoicingPdfImage build() throws IOException {
        return new InvoicingPdfImage(document, contentStream, imagePath, dimensions);
    }

    InvoicingPdfImageBuilder document(PDDocument document) {
        this.document = document;
        return this;
    }

    InvoicingPdfImageBuilder contentStream(PDPageContentStream contentStream) {
        this.contentStream = contentStream;
        return this;
    }

    InvoicingPdfImageBuilder imagePath(Path imagePath) {
        this.imagePath = imagePath;
        return this;
    }

    InvoicingPdfImageBuilder dimensions(PdfDimensions dimensions) {
        this.dimensions = dimensions;
        return this;
    }

}
