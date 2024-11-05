package com.jovisco.tutorial.invoicingpdf;

public record CreatePdfRequest(
        String invoiceId,
        String formattedInvoiceDate,
        String customerId,
        String title,
        String[] addressLines,
        String billingPeriod,
        String referenceTitle,
        String columnsHeader,
        String totalsHeader,
        String formattedTotalNetAmount)
{}
