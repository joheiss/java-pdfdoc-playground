package com.jovisco.tutorial.invoicingpdf;

public record PdfDimensions(float x, float y, float width, float height) {

    public static PdfDimensions calculateDimensions(PdfDimensions dimensions) {
        var coords = PdfPageCoordsOnA4.ofTopLeftWithHeightInMM(
                dimensions.x(), dimensions.y(), dimensions.height());
        return new PdfDimensions(
                coords.xPos(),
                coords.yPos(),
                dimensions.width(),
                dimensions.height());
    }
}
