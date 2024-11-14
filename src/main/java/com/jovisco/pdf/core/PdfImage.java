package com.jovisco.pdf.core;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class PdfImage implements PdfElement {

    private final PDPageContentStream cs;
    private final PDImageXObject image;
    private final PdfDimensions dimensions;

    public static PdfImageBuilder builder() {
        return new PdfImageBuilder();
    }

    public PdfImage(PDDocument doc, PDPageContentStream cs, Path imagePath, PdfDimensions dimensions) {
        this.cs = cs;
        this.dimensions = dimensions;
        this.image = loadImage(imagePath, doc);
    }

   private PDImageXObject loadImage(Path imagePath, PDDocument doc) {
       try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
           var future = executor.submit(() ->
                   PDImageXObject.createFromFile(imagePath.toAbsolutePath().toString(), doc));
           return future.get();
       } catch (ExecutionException | InterruptedException e) {
           throw new PdfImageLoadingException(e.getMessage());
       }
   }

    @Override
    public void print() throws IOException {
        cs.drawImage(image, dimensions.x(), dimensions.y(), dimensions.width(), dimensions.height());
    }
}
