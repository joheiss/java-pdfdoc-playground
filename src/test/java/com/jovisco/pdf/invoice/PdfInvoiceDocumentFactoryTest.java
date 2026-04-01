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
import java.math.RoundingMode;
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
        var filePath = "target/R" + request.invoiceId() + ".pdf";
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
        var filePath = "target/R" + request.invoiceId() + "_X.pdf";
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
            // assertEquals(new BigDecimal(totalGrossAmount), tc.getGrandTotal());
        } catch (XPathExpressionException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private CreatePdfInvoiceRequest makeCreateInvoiceDocumentRequest() {

        var hourlyRate = 70.00;
        var hours = 176;
        var totalNetAmount = hourlyRate * hours;
        var bd = new BigDecimal(totalNetAmount * 0.19);

        var totalVatAmount = bd.setScale(2, RoundingMode.HALF_UP).doubleValue();
        var totalGrossAmount = totalNetAmount + totalVatAmount;

        return new CreatePdfInvoiceRequest(
                5224,
                LocalDate.of(2026, 4, 2),
                1014,
                List.of("DEED Consulting GmbH", "", "Karl-Benz-Str. 9", "40764 Langenfeld(Rhld.)"),
                4131,
                "PVRA250327FJH - Leistungszeitraum März 2026",
                "EUR",
                19.0,
                List.of(new CreatePdfInvoiceItemRequest(1,hours,"Arbeitsstunden F.J. Heiß (remote)", hourlyRate, totalNetAmount)
                        ),
                totalNetAmount,
                totalVatAmount,
                totalGrossAmount,
                "Zahlbar innerhalb von 45 Tagen ohne Abzug",
                "PVRA250327FJH",
                // List.of("Eingesetzter Mitarbeiter: Franz Josef Heiß")
                List.of()
        );
    }

}