package com.jovisco.pdf.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PdfBlock implements PdfElement {

    private final List<PdfElement> elements = new ArrayList<>();

    public PdfBlock(PdfElement... components) {
        elements.addAll(Arrays.asList(components));
    }

    @Override
    public void print() throws IOException {
        for (var element : elements) {
            element.print();
        }
    }
}
