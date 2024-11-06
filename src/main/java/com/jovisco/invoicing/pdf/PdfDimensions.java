package com.jovisco.invoicing.pdf;

public record PdfDimensions(float x, float y, float width, float height) {

    private final static float PAGE_WIDTH = 595.28f;
    private final static float PAGE_HEIGHT = 841.89f;
    private final static float PAGE_WIDTH_MM = 210.1f;
    private final static float PAGE_HEIGHT_MM = 297.1f;
    public final static float PAGE_WIDTH_FACTOR = PAGE_WIDTH / PAGE_WIDTH_MM;
    private final static float PAGE_HEIGHT_FACTOR = PAGE_HEIGHT / PAGE_HEIGHT_MM;

    public static PdfDimensions ofA4(float x, float y, float width, float height) {
        var xPos = x * PAGE_WIDTH_FACTOR;
        var yPos = (PAGE_HEIGHT_MM - y) * PAGE_HEIGHT_FACTOR;
        return new PdfDimensions(xPos, yPos, width, height);
    }

    public static PdfDimensions ofA4mm(float x, float y, float width, float height) {
        var xPos = x * PAGE_WIDTH_FACTOR;
        var yPos = (PAGE_HEIGHT_MM - y) * PAGE_HEIGHT_FACTOR - (height / 2.0f);
        return new PdfDimensions(xPos, yPos, width, height);
    }
}
