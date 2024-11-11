package com.jovisco.pdf.base;

import com.jovisco.pdf.core.RequestMap;

public class PdfBaseTemplateGeneratorDEde extends PdfBaseTemplateGenerator {


    public PdfBaseTemplateGeneratorDEde(RequestMap requestMap) {
        super(requestMap);
    }

    protected void fillMetaInformation() {
        super.fillMetaInformation();
        var metadata = template.getDocumentInformation();
        metadata.setKeywords("Jovisco, Template, Invoicing, DE_de");
    }
}