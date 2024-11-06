package com.jovisco.invoicing.pdf;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;

public class PdfTextBlock {

    private final PDPageContentStream cs;
    private final String[] textLines;
    private final PdfDimensions dimensions;
    private PDType1Font font = null;
    private float[] color = null;
    private float leading = 15.0f;

    public static PdfTextBlockBuilder builder() {
        return new PdfTextBlockBuilder();
    }

    public PdfTextBlock(PDPageContentStream cs, String[] textLines, PdfDimensions dimensions, PDType1Font font,
                        int[] color, float leading) {
        this(cs, textLines, dimensions, font);
        if (color != null) this.color = new float[]{color[0]/255.0f, color[1]/255.0f, color[2]/255.0f};
        if (leading > 0) this.leading = leading;
    }

    public PdfTextBlock(PDPageContentStream cs, String[] textLines, PdfDimensions dimensions, PDType1Font font) {
        this(cs, textLines, dimensions);
        this.font = font;
    }

    public PdfTextBlock(PDPageContentStream cs, String[] textLines, PdfDimensions dimensions) {
        this.cs = cs;
        this.textLines = textLines;
        this.dimensions = dimensions;
    }

    public void printTextBlock() throws IOException {
        cs.beginText();
        if (font != null) cs.setFont(font, dimensions.height());
        if (color != null) cs.setNonStrokingColor(this.color[0], this.color[1], this.color[2]);
        if (leading > 0) cs.setLeading(this.leading);
        cs.newLineAtOffset(dimensions.x(), dimensions.y());
        for (var textLine : textLines) {
            cs.showText(textLine);
            cs.newLine();
        }
        cs.endText();
    }


}
