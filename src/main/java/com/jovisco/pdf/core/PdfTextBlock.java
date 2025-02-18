package com.jovisco.pdf.core;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.IOException;
import java.util.List;

public class PdfTextBlock implements PdfElement{

    private final PDPageContentStream cs;
    private final List<String> textLines;
    private final PdfDimensions dimensions;
    private PDType0Font font = null;
    private float[] color = null;
    private float leading = 15.0f;

    public static PdfTextBlockBuilder builder() {
        return new PdfTextBlockBuilder();
    }

    public PdfTextBlock(PDPageContentStream cs, List<String> textLines, PdfDimensions dimensions, PDType0Font font,
                        int[] color, float leading) {
        this(cs, textLines, dimensions, font);
        if (color != null) this.color = new float[]{color[0]/255.0f, color[1]/255.0f, color[2]/255.0f};
        if (leading > 0) this.leading = leading;
    }

    public PdfTextBlock(PDPageContentStream cs, List<String> textLines, PdfDimensions dimensions, PDType0Font font) {
        this(cs, textLines, dimensions);
        this.font = font;
    }

    public PdfTextBlock(PDPageContentStream cs, List<String> textLines, PdfDimensions dimensions) {
        this.cs = cs;
        this.textLines = textLines;
        this.dimensions = dimensions;
    }

    @Override
    public void print() throws IOException {
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
