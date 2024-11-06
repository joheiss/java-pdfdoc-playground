package com.jovisco.invoicing.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.io.IOException;
import java.nio.file.Path;

public class PdfImageBuilder {

    private PDDocument document;
    private PDPageContentStream contentStream;
    private Path imagePath;
    private PdfDimensions dimensions;

    PdfImageBuilder() {}

   PdfImage build() throws IOException {
        return new PdfImage(document, contentStream, imagePath, dimensions);
    }

    PdfImageBuilder document(PDDocument document) {
        this.document = document;
        return this;
    }

    PdfImageBuilder contentStream(PDPageContentStream contentStream) {
        this.contentStream = contentStream;
        return this;
    }

    PdfImageBuilder imagePath(Path imagePath) {
        this.imagePath = imagePath;
        return this;
    }

    PdfImageBuilder dimensions(PdfDimensions dimensions) {
        this.dimensions = dimensions;
        return this;
    }

}
