package com.jovisco.tutorial.invoicingpdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.IOException;
import java.nio.file.Path;

public class InvoicingPdfTemplateImage {

    private final PDImageXObject image;
    private final PdfDimensions dimensions;

    public InvoicingPdfTemplateImage(PDDocument doc, Path imagePath, PdfDimensions dimensions) throws IOException {
        this.dimensions = PdfDimensions.calculateDimensions(dimensions);
        this.image = loadImage(imagePath, doc);
    }

   private PDImageXObject loadImage(Path imagePath, PDDocument doc) throws IOException {
        return PDImageXObject.createFromFile(imagePath.toAbsolutePath().toString(), doc);
    }

    public void draw(PDPageContentStream cs) throws IOException {
        cs.drawImage(image, dimensions.x(), dimensions.y(), dimensions.width(), dimensions.height());
    }
}
