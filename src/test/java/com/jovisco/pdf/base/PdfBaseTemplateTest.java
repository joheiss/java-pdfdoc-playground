package com.jovisco.pdf.base;

import com.jovisco.pdf.core.RequestMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

class PdfBaseTemplateTest {

    RequestMap requestMap;

    @BeforeEach
    void setup() {
        requestMap = new RequestMap(new HashMap<>());
    }
    @Test
    @DisplayName("should create a base template pdf and save it at the given path")
    void test_create() {
        var templateFilePath = "target/test-basetemplate.pdf";
        var templateGenerator = new PdfBaseTemplateGeneratorDEde(requestMap);
        var template = new PdfBaseTemplate(templateGenerator, templateFilePath);

        template.create();
    }
}