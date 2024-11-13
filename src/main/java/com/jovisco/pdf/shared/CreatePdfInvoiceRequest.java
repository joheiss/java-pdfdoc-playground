package com.jovisco.pdf.shared;

import java.time.LocalDate;
import java.util.List;

public record CreatePdfInvoiceRequest(
        int invoiceId,
        LocalDate invoiceDate,
        int customerId,
        List<String> address,
        String billingPeriod,
        String currencyCode,
        double vatPercentage,
        List<CreatePdfInvoiceItemRequest> items,
        double totalNetAmount,
        double totalVatAmount,
        double totalGrossAmount,
        String paymentTerms
) {
}
