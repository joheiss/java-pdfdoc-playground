package com.jovisco.pdf.core;

public record PdfDimensions(float x, float y, float width, float height) {

    public final static float PAGE_WIDTH = 595.28f;
    public final static float PAGE_HEIGHT = 841.89f;
    public final static float PAGE_WIDTH_MM = 210.1f;
    public final static float PAGE_HEIGHT_MM = 297.1f;
    public final static float PAGE_WIDTH_FACTOR = PAGE_WIDTH / PAGE_WIDTH_MM;
    public final static float PAGE_HEIGHT_FACTOR = PAGE_HEIGHT / PAGE_HEIGHT_MM;

    /**
     * Recalculates the y coordinate to be based on bottom-left corner instead of top-left.
     * @param x - x coordinate in pt
     * @param y - y coordinate in pt based on top-left corner
     * @param width - in pt
     * @param height - in pt
     * @return PdfDimensions
     */
    public static PdfDimensions ofA4(float x, float y, float width, float height) {
        var yPos = (PAGE_HEIGHT - y);
        return new PdfDimensions(x, yPos, width, height);
    }

    /**
     * <p>Convert element dimensions x and y from millimeters (mm) to Adobe points (pt),
     * and recalculate y to be based on the bottom left instead of top left.</p>
     *
     * <p><b>IMPORTANT:</b></p>
     * this convenience function is only re-calculating x and y.
     * Height and width must be provided in Pt.</p>
     *
     * @param xInMM - x coordinate in millimeters
     * @param yInMM - y coordinate in millimeters, measured from the top-left corner
     * @param widthInPt - width in points
     * @param heightInPt - height in points
     * @return PdfDimension in points, x, y based on bottom-left corner
     */
    public static PdfDimensions ofA4mm(float xInMM, float yInMM, float widthInPt, float heightInPt) {
        var xPos = xInMM * PAGE_WIDTH_FACTOR;
        var yPos = (PAGE_HEIGHT_MM - yInMM) * PAGE_HEIGHT_FACTOR - (heightInPt / 2.0f);
        return new PdfDimensions(xPos, yPos, widthInPt, heightInPt);
    }
}
