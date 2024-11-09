package com.jovisco.pdf.core;

import org.apache.pdfbox.pdmodel.PDDocument;

import java.nio.file.Paths;

public interface PdfDocumentGenerator {

    PDDocument generate();

    default boolean documentExists(String filePath) {
        return Paths.get(filePath).toFile().exists();
    }
}
