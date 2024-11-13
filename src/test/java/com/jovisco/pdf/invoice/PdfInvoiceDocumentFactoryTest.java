package com.jovisco.pdf.invoice;

import com.jovisco.pdf.shared.CreatePdfInvoiceItemRequest;
import com.jovisco.pdf.shared.CreatePdfInvoiceRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class PdfInvoiceDocumentFactoryTest {

    @Test
    @DisplayName("should return a PdfInvoiceDocument class")
    void test_class_creation() {
        var request = makeCreateInvoiceDocumentRequest();
        var creator = new PdfInvoiceDocumentFactory().getInvoiceCreator(
                Locale.getDefault(),
                request,
                "target/test-TR" + request.invoiceId() + ".pdf"
        );
        assertInstanceOf(PdfInvoiceDocument.class, creator);
        creator.create();
    }

    private CreatePdfInvoiceRequest makeCreateInvoiceDocumentRequest() {

        return new CreatePdfInvoiceRequest(
                5277,
                LocalDate.of(2024, 11, 24),
                1007,
                List.of("Schniedelwutz AG", "", "Postfach 1234", "77777 Schwieberdingen"),
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
                "Zahlbar innerhalb von 30 Tagen ohne Abzug"
        );
    }

}