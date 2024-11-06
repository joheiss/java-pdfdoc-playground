package com.jovisco.invoicing.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;

public interface PdfDocumentGenerator {

    PDDocument generate();
    boolean documentExists(String filePath);
}
