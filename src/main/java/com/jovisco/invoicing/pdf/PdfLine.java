package com.jovisco.invoicing.pdf;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.io.IOException;

public class PdfLine {

    private final PDPageContentStream cs;
    private final PdfDimensions dimensions;
    private float[] color = null;

    public static PdfLineBuilder builder() {
        return new PdfLineBuilder();
    }

    public PdfLine(PDPageContentStream cs, PdfDimensions dimensions, int[] color) {
        this(cs, dimensions);
        if (color != null) this.color = new float[]{color[0]/255.0f, color[1]/255.0f, color[2]/255.0f};
    }

    public PdfLine(PDPageContentStream cs, PdfDimensions dimensions) {
        this.cs = cs;
        this.dimensions = dimensions;
    }

    public void draw() throws IOException {
        if (this.color != null) cs.setStrokingColor(this.color[0], this.color[1], this.color[2]);
        cs.moveTo(dimensions.x(), dimensions.y());
        cs.lineTo(dimensions.x() + dimensions.width(), dimensions.y());
        cs.stroke();
    }
}
