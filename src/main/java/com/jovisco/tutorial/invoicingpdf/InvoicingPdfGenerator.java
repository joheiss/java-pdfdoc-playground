package com.jovisco.tutorial.invoicingpdf;

import org.apache.pdfbox.pdmodel.PDDocument;

public interface InvoicingPdfGenerator {

    PDDocument generate();
    boolean documentExists(String filePath);
}
