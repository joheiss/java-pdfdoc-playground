package com.jovisco.invoicing.pdf;

public class PdfInvoiceTemplateGeneratorDEde extends PdfInvoiceTemplateGenerator {

    public PdfInvoiceTemplateGeneratorDEde(
            CreatePdfInvoiceTemplateRequest request,
            String baseTemplateFilePath,
            String targetFilePath)
    {
        super(request, baseTemplateFilePath, targetFilePath);
    }

    protected void fillMetaInformation() {
        super.fillMetaInformation();
        var metadata = template.getDocumentInformation();
        metadata.setKeywords("Jovisco, Template, Invoice, Invoicing, DE_de");
    }
}