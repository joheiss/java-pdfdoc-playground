package com.jovisco.invoicing.pdf;

public record CreatePdfInvoiceTemplateRequest(
        String title,
        String referenceTitle,
        String customerIdLabel,
        String invoiceIdLabel,
        String invoiceDateLabel,
        String itemIdHeader,
        String itemQuantityHeader,
        String itemUnitNetAmountHeader,
        String itemTotalNetAmountHeader,
        String columnsHeader)
{}
