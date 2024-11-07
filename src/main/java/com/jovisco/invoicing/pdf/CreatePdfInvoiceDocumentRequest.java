package com.jovisco.invoicing.pdf;

import java.util.List;

public record CreatePdfInvoiceDocumentRequest(
        String invoiceId,
        String formattedInvoiceDate,
        String customerId,
        List<String> addressLines,
        String billingPeriod,
        String formattedTotalNetAmount,
        String formattedTotalVatAmount,
        String formattedTotalGrossAmount,
        List<CreatePdfInvoiceItemRequest> items,
        String totalsHeader,
        String paymentTerms,
        List<String> optionalInvoiceTexts)
{}
