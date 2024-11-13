package com.jovisco.pdf.base;

import com.jovisco.pdf.core.RequestMap;

public class PdfBaseTemplateGenerator_deDE extends PdfBaseTemplateGenerator {


    public PdfBaseTemplateGenerator_deDE(RequestMap requestMap) {
        super(requestMap);
    }

    protected void fillMetaInformation() {
        super.fillMetaInformation();
        var metadata = template.getDocumentInformation();
        metadata.setKeywords("Jovisco, Template, Invoicing, DE_de");
    }
}