package com.jovisco.tutorial.invoicingpdf;

public record CreatePdfInvoiceTemplateRequest(
        String title,
        String referenceTitle,
        String customerIdLabel,
        String invoiceIdLabel,
        String invoiceDateLabel,
        String columnsHeader)
{}
