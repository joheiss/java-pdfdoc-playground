package com.jovisco.pdf.invoice;

import com.jovisco.pdf.core.PdfDocumentCreator;
import com.jovisco.pdf.core.PdfDocumentGenerator;
import com.jovisco.pdf.core.RequestMap;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.io.IOException;
import java.util.Calendar;

@RequiredArgsConstructor
public class PdfInvoiceDocument implements PdfDocumentCreator {

    private final PdfDocumentGenerator baseTemplateGenerator;
    private final PdfDocumentGenerator invoiceTemplateGenerator;
    private final PdfDocumentGenerator invoiceDocumentGenerator;
    private final String filePath;

    public void create() {
        try (var document = new PDDocument()) {
            var page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            baseTemplateGenerator.generate(document);
            invoiceTemplateGenerator.generate(document);
            invoiceDocumentGenerator.generate(document);
            fillMetaInformation(document);
            document.save(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void fillMetaInformation(PDDocument doc) {

        var now = Calendar.getInstance();
        var invoiceId = invoiceDocumentGenerator.getRequestMap().get(RequestMap.INVOICE_ID);

        var metadata = doc.getDocumentInformation();
        metadata.setTitle("Jovisco GmbH - Invoice " + invoiceId);
        metadata.setAuthor("Jo Heiss");
        metadata.setSubject("Invoice " + invoiceId);
        metadata.setCreationDate(now);
        metadata.setModificationDate(now);
        metadata.setKeywords("Jovisco, Invoice, " + invoiceId);
        metadata.setProducer("PDFBox");
    }

}
