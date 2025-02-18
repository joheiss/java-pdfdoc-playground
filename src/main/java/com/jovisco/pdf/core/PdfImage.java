package com.jovisco.pdf.core;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
        this.image.getCOSObject().setItem(COSName.SMASK, null);
    }

   private PDImageXObject loadImage(Path imagePath, PDDocument doc) {
       try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
           var file = new File(imagePath.toAbsolutePath().toString());
           var future = executor.submit(() ->
                   PDImageXObject.createFromFileByExtension(file, doc));
           return future.get(500, TimeUnit.MILLISECONDS);
       } catch (ExecutionException | InterruptedException | TimeoutException e) {
           throw new PdfImageLoadingException(e.getMessage());
       }
   }

    @Override
    public void print() throws IOException {
        cs.drawImage(image, dimensions.x(), dimensions.y(), dimensions.width(), dimensions.height());
    }
}
