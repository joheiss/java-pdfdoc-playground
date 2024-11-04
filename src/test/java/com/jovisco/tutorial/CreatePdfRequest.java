package com.jovisco.tutorial;

public record CreatePdfRequest(
        String invoiceId,
        String formattedInvoiceDate,
        String customerId,
        String title,
        String[] addressLines,
        String billingPeriod,
        String formattedTotalNetAmount)
{}
