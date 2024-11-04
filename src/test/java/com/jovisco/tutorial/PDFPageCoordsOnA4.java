package com.jovisco.tutorial;

public record PDFPageCoordsOnA4(float xPos, float yPos) {

    private final static float PAGE_WIDTH = 595.28f;
    private final static float PAGE_HEIGHT = 841.89f;
    private final static float PAGE_WIDTH_MM = 210.1f;
    private final static float PAGE_HEIGHT_MM = 297.1f;
    public final static float PAGE_WIDTH_FACTOR = PAGE_WIDTH / PAGE_WIDTH_MM;
    private final static float PAGE_HEIGHT_FACTOR = PAGE_HEIGHT / PAGE_HEIGHT_MM;

    public static PDFPageCoordsOnA4 ofTopLeftInMM(float xPosInMM, float yPosInMM) {
        var xPos = xPosInMM * PAGE_WIDTH_FACTOR;
        var yPos = (PAGE_HEIGHT_MM - yPosInMM) * PAGE_HEIGHT_FACTOR;
        return new PDFPageCoordsOnA4(xPos, yPos);
    }

    public static PDFPageCoordsOnA4 ofTopLeftWithHeightInMM(float xPosInMM, float yPosInMM, float height) {
        var xPos = xPosInMM * PAGE_WIDTH_FACTOR;
        var yPos = (PAGE_HEIGHT_MM - yPosInMM) * PAGE_HEIGHT_FACTOR - (height / 2.0f);
        return new PDFPageCoordsOnA4(xPos, yPos);
    }
}
