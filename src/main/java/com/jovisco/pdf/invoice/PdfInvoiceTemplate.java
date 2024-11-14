package com.jovisco.pdf.invoice;

import com.jovisco.pdf.core.PdfDocumentCreator;
import com.jovisco.pdf.core.PdfDocumentException;
import com.jovisco.pdf.core.PdfDocumentGenerator;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.io.IOException;
import java.util.Calendar;

@RequiredArgsConstructor
public class PdfInvoiceTemplate implements PdfDocumentCreator {

    private final PdfDocumentGenerator baseTemplateGenerator;
    private final PdfDocumentGenerator invoiceTemplateGenerator;
    private final String filePath;

    public void create() {
        try (var template = new PDDocument()) {
            var page = new PDPage(PDRectangle.A4);
            template.addPage(page);
            baseTemplateGenerator.generate(template);
            invoiceTemplateGenerator.generate(template);
            fillMetaInformation(template);
            template.save(filePath);
        } catch (IOException e) {
            throw new PdfDocumentException(e.getMessage());
        }
    }

    private void fillMetaInformation(PDDocument template) {

        var now = Calendar.getInstance();

        var metadata = template.getDocumentInformation();
        metadata.setTitle("Jovisco GmbH - Invoice Template");
        metadata.setAuthor("Jo Heiss");
        metadata.setSubject("Invoice Template");
        metadata.setCreationDate(now);
        metadata.setModificationDate(now);
        metadata.setKeywords("Jovisco, Template, Invoice, Invoicing");
        metadata.setProducer("PDFBox");
    }
}
