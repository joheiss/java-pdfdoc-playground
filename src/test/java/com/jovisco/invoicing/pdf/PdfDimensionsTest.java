package com.jovisco.invoicing.pdf;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PdfDimensionsTest {

    @Test
    @DisplayName("x, width and height remain as-is, y is re-calculated to bottom-based")
    void ofA4() {
        var dimensions = PdfDimensions.ofA4(10.0f, 100.0f, 20.0f, 1.0f);
        assertEquals(10.0f, dimensions.x());
        assertEquals(20.0f, dimensions.width());
        assertEquals(1.0f, dimensions.height());
        var expectedY = PdfDimensions.PAGE_HEIGHT - 100.0f;
        assertEquals(expectedY, dimensions.y());
    }

    @Test
    @DisplayName("width and height remain as-is, x, y are converted and re-calculated to bottom-based")
    void ofA4mm() {
        var x = 10.0f;
        var y = 100.0f;
        var width = 20.0f;
        var height = 2.0f;
        var dimensions = PdfDimensions.ofA4mm(x, y, width, height);
        assertEquals(10.0f * PdfDimensions.PAGE_WIDTH_FACTOR, dimensions.x());
        assertEquals(20.0f, dimensions.width());
        assertEquals(2.0f, dimensions.height());
        var expectedY = (PdfDimensions.PAGE_HEIGHT_MM - y) * PdfDimensions.PAGE_HEIGHT_FACTOR - (height / 2.0f);
        assertEquals(expectedY, dimensions.y());
    }
}