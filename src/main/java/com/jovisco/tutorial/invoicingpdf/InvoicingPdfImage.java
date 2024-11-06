package com.jovisco.tutorial.invoicingpdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.IOException;
import java.nio.file.Path;

public class InvoicingPdfImage {

    private final PDPageContentStream cs;
    private final PDImageXObject image;
    private final PdfDimensions dimensions;

    public static InvoicingPdfImageBuilder builder() {
        return new InvoicingPdfImageBuilder();
    }

    public InvoicingPdfImage(PDDocument doc, PDPageContentStream cs, Path imagePath, PdfDimensions dimensions) throws IOException {
        this.cs = cs;
        this.dimensions = PdfDimensions.calculateDimensions(dimensions);
        this.image = loadImage(imagePath, doc);
    }

   private PDImageXObject loadImage(Path imagePath, PDDocument doc) throws IOException {
        return PDImageXObject.createFromFile(imagePath.toAbsolutePath().toString(), doc);
    }

    public void draw() throws IOException {
        cs.drawImage(image, dimensions.x(), dimensions.y(), dimensions.width(), dimensions.height());
    }
}