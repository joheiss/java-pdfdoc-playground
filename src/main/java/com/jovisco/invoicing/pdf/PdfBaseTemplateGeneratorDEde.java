package com.jovisco.invoicing.pdf;

public class PdfBaseTemplateGeneratorDEde extends PdfBaseTemplateGenerator {

    public PdfBaseTemplateGeneratorDEde(String filePath) {
        super(filePath);
    }

    protected void fillMetaInformation() {
        super.fillMetaInformation();
        var metadata = template.getDocumentInformation();
        metadata.setKeywords("Jovisco, Template, Invoicing, DE_de");
    }
}