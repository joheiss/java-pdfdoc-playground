package com.jovisco.tutorial.invoicingpdf;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.io.IOException;

public class InvoicingPdfLine {

    private final PDPageContentStream cs;
    private final PdfDimensions dimensions;
    private float[] color = null;

    public static InvoicingPdfLineBuilder builder() {
        return new InvoicingPdfLineBuilder();
    }

    public InvoicingPdfLine(PDPageContentStream cs, PdfDimensions dimensions, int[] color) {
        this(cs, dimensions);
        if (color != null) this.color = new float[]{color[0]/255.0f, color[1]/255.0f, color[2]/255.0f};
    }

    public InvoicingPdfLine(PDPageContentStream cs, PdfDimensions dimensions) {
        this.cs = cs;
        this.dimensions = PdfDimensions.calculateDimensions(dimensions);
    }

    public void draw() throws IOException {
        if (this.color != null) cs.setStrokingColor(this.color[0], this.color[1], this.color[2]);
        cs.moveTo(dimensions.x(), dimensions.y());
        cs.lineTo(dimensions.x() + dimensions.width(), dimensions.y());
        cs.stroke();
    }
}
