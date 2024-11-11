package com.jovisco.pdf.base;

import com.jovisco.pdf.core.RequestMap;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PdfBaseTemplateTest {

    static final String templateFilePath = "target/test-basetemplate.pdf";
    RequestMap requestMap;

    @BeforeEach
    void setup() {
        requestMap = new RequestMap(new HashMap<>());
    }

    @Order(10)
    @Test
    @DisplayName("should create a base template pdf and save it at the given path")
    void test_create() {

        var templateGenerator = new PdfBaseTemplateGeneratorDEde(requestMap);
        var template = new PdfBaseTemplate(templateGenerator, templateFilePath);

        template.create();
    }

    @Order(11)
    @Test
    @DisplayName("should be accessible")
    void should_be_accessible() {
        try (var doc = Loader.loadPDF(new File(templateFilePath))) {
            var pdfStripper = new PDFTextStripper();
            //Retrieving text from PDF document
            var text = pdfStripper.getText(doc);
            // contains at least a line feed
            assertFalse(text.isEmpty());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Order(12)
    @Test
    @DisplayName("should throw an exception if target file cannot be saved")
    void should_throw_if_file_not_saved() {
        assertThrows(RuntimeException.class, () -> {
            var templateGenerator = new PdfBaseTemplateGeneratorDEde(requestMap);
            var template = new PdfBaseTemplate(templateGenerator, "NOACCESS/test-basetemplate.pdf");
            template.create();
        });
    }
}