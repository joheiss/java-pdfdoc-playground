package com.jovisco.pdf.invoice;

import com.jovisco.pdf.shared.CreatePdfInvoiceItemRequest;
import com.jovisco.pdf.shared.CreatePdfInvoiceRequest;
import com.jovisco.pdf.shared.PdfInvoiceDocumentFactory;
import com.jovisco.pdf.xinvoice.XInvoiceDocument;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mustangproject.Invoice;
import org.mustangproject.ZUGFeRD.TransactionCalculator;
import org.mustangproject.ZUGFeRD.ZUGFeRDInvoiceImporter;

import javax.xml.xpath.XPathExpressionException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import static com.jovisco.pdf.xinvoice.XInvoiceUtils.fromLocalDate;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class PdfInvoiceDocumentFactoryTest {

    @Test
    @DisplayName("should create an X-Invoice")
    void test_class_creation() {
        var request = makeCreateInvoiceDocumentRequest();
        var filePath = "target/test-TR" + request.invoiceId() + ".pdf";
        var creator = new PdfInvoiceDocumentFactory().getInvoiceCreator(
                Locale.getDefault(),
                request,
                filePath
        );
        assertInstanceOf(PdfInvoiceDocument.class, creator);
        creator.create();
        // create XInvoice
        var xInvoiceDocument = new XInvoiceDocument(request, filePath);
        xInvoiceDocument.create();
    }

    @Test@DisplayName("should be able to read all relevant data from the X- Invoice")
    void test_read_x_invoice() {
        var request = makeCreateInvoiceDocumentRequest();
        var filePath = "target/test-TR" + request.invoiceId() + "_X.pdf";
        ZUGFeRDInvoiceImporter zii = new ZUGFeRDInvoiceImporter(filePath);
        try {
            Invoice invoice = zii.extractInvoice();
            assertEquals(request.invoiceId(), Integer.parseInt(invoice.getNumber()));
            assertEquals(fromLocalDate(request.invoiceDate()), invoice.getIssueDate());
            assertEquals(request.items().size(), invoice.getZFItems().length);
            var tc = new TransactionCalculator(invoice);
            log.info("Tax Basis: {}", tc.getTaxBasis());
            log.info("Value: {}", tc.getValue());
            log.info("Charge Total: {}", tc.getChargeTotal());
            log.info("Due Payable: {}", tc.getDuePayable());
            log.info("Allowance Total: {}", tc.getAllowanceTotal());
            log.info("Grand Total: {}", tc.getGrandTotal());
            assertEquals(new BigDecimal("17270.11"), tc.getGrandTotal());
        } catch (XPathExpressionException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private CreatePdfInvoiceRequest makeCreateInvoiceDocumentRequest() {

        return new CreatePdfInvoiceRequest(
                5277,
                LocalDate.of(2024, 11, 24),
                1007,
                List.of("Schniedelwutz AG", "", "Postfach 1234", "77777 Schwieberdingen"),
                4412,
                "Leistungszeitraum Oktober 2024",
                "EUR",
                19.0,
                List.of(new CreatePdfInvoiceItemRequest(1,100,"Arbeitsstunden im Projekt XYZ", 123.45, 12345.00),
                        new CreatePdfInvoiceItemRequest(2,10,"Reisestunden im Projekt XYZ", 66.77, 667.70),
                        new CreatePdfInvoiceItemRequest(3,2,"Übernachtungen", 100.00, 200.00),
                        new CreatePdfInvoiceItemRequest(4,1000,"Gefahrene Km", 1.30, 1300.00)
                        ),
                14500.00,
                3500.00,
                18000.00,
                "Zahlbar innerhalb von 30 Tagen ohne Abzug",
                """
                    Bitte beachten sie den geänderten Mehrwertsteuersatz von nun 25%. Der neue Mehrwertsteuersatz wird 
                    auf dieser Rechnung, entsprechend dem gesetzlichen Stichtag, bereits angewendet.
                    """
        );
    }

}