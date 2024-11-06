package com.jovisco.invoicing.pdf;

public record CreatePdfInvoiceItemRequest(
        String itemId,
        String formattedQuantity,
        String description,
        String formattedUnitNetAmount,
        String formattedTotalNetAmount)
{}
