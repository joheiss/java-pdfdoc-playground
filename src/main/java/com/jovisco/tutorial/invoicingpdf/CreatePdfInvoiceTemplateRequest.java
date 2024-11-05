package com.jovisco.tutorial.invoicingpdf;

public record CreatePdfInvoiceTemplateRequest(
        String title,
        String referenceTitle,
        String columnsHeader,
        String totalsHeader)
{}
