package com.jovisco.pdf.shared;

import com.jovisco.pdf.base.PdfBaseTemplateGenerator_deDE;
import com.jovisco.pdf.core.PdfDocumentCreator;
import com.jovisco.pdf.invoice.PdfInvoiceDocument;
import com.jovisco.pdf.invoice.PdfInvoiceDocumentGenerator_deDE;
import com.jovisco.pdf.invoice.PdfInvoiceTemplateGenerator_deDE;

import java.util.Locale;

public class PdfInvoiceDocumentFactory {

    public PdfDocumentCreator getInvoiceCreator(Locale locale, CreatePdfInvoiceRequest request, String filePath) {
        if (locale.toString().equals("de_DE")) {
              var requestMap = new PdfInvoiceRequestAdapter_deDE(locale).map(request);
              var baseTemplateGenerator = new PdfBaseTemplateGenerator_deDE(requestMap);
              var invoiceTemplateGenerator = new PdfInvoiceTemplateGenerator_deDE(requestMap);
              var invoiceDocumentGenerator = new PdfInvoiceDocumentGenerator_deDE(requestMap);
              return new PdfInvoiceDocument(
                      baseTemplateGenerator, invoiceTemplateGenerator, invoiceDocumentGenerator, filePath);
        }
        return null;
    }
}
