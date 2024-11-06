package com.jovisco.tutorial.invoicingpdf;

public record CreatePdfInvoiceDocumentRequest(
        String invoiceId,
        String formattedInvoiceDate,
        String customerId,
        String[] addressLines,
        String billingPeriod,
        String formattedTotalNetAmount,
        String formattedTotalVatAmount,
        String formattedTotalGrossAmount,
        CreatePdfInvoiceItemRequest[] items,
        String totalsHeader,
        String paymentTerms,
        String[] optionalInvoiceTexts)
{}
