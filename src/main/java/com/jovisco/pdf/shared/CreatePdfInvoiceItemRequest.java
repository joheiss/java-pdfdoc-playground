package com.jovisco.pdf.shared;

public record CreatePdfInvoiceItemRequest(
        int itemId,
        double quantity,
        String description,
        double unitNetAmount,
        double totalNetAmount
) {
}
