package com.jovisco.tutorial.invoicingpdf;

public record CreatePdfInvoiceItemRequest(
        String itemId,
        String formattedQuantity,
        String description,
        String formattedUnitNetAmount,
        String formattedTotalNetAmount)
{}
