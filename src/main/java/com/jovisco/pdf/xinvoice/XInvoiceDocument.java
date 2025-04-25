package com.jovisco.pdf.xinvoice;

import com.jovisco.pdf.shared.CreatePdfInvoiceRequest;
import lombok.SneakyThrows;
import org.mustangproject.*;
import org.mustangproject.ZUGFeRD.PDFAConformanceLevel;
import org.mustangproject.ZUGFeRD.Profile;
import org.mustangproject.ZUGFeRD.Profiles;
import org.mustangproject.ZUGFeRD.ZUGFeRDExporterFromPDFA;

import java.math.BigDecimal;

import static com.jovisco.pdf.xinvoice.XInvoiceUtils.fromLocalDate;

public class XInvoiceDocument {

    private final CreatePdfInvoiceRequest request;
    private final String filePath;

    public XInvoiceDocument(CreatePdfInvoiceRequest request, String filePath) {
        this.request = request;
        this.filePath = filePath;

    }

    @SneakyThrows
    public void create() {
        try (var exporter = new ZUGFeRDExporterFromPDFA().load(filePath)) {
            exporter.setZUGFeRDVersion(2);
            exporter.setConformanceLevel(PDFAConformanceLevel.UNICODE);
            exporter.setProfile(Profiles.getByName("EXTENDED"));
            // exporter.setProfile(new Profile("EXTENDED", "EXTENDED"));
            exporter.setProducer("FacturX");
            exporter.setCreator(System.getProperty("user.name"));
            exporter.setTransaction(buildInvoiceData());
            exporter.export(buildXInvoiceFilePath(filePath));
        }
    }

    private Invoice buildInvoiceData() {
        var invoice = new Invoice();
        invoice.setCurrency(request.currencyCode());
        invoice.setNumber(String.valueOf(request.invoiceId()));
        invoice.setDocumentCode("380");
        invoice.setDocumentName("Rechnung");
        invoice.setIssueDate(fromLocalDate(request.invoiceDate()));
        var deliveryDate = request.invoiceDate().withDayOfMonth(1).minusDays(1);
        invoice.setDeliveryDate(fromLocalDate(deliveryDate));
        var recipient = new TradeParty();
        recipient.setName(request.address().getFirst());
        recipient.setStreet(request.address().get(2));
        recipient.setLocation(request.address().get(3).substring(5));
        recipient.setZIP(request.address().get(3).substring(0,5));
        recipient.setCountry("DE");
        invoice.setRecipient(recipient);
        var sender = new TradeParty();
        sender.setName("Jovisco GmbH");
        sender.setStreet("Südstr. 9");
        sender.setLocation("Langenbrettach");
        sender.setZIP("74243");
        sender.setCountry("DE");
        sender.addVATID("DE261938986");
        var bankDetails = new BankDetails();
        bankDetails.setIBAN("DE65600501010002276155");
        bankDetails.setBIC("SOLADEST600");
        bankDetails.setAccountName("Jovisco GmbH");
        sender.addBankDetails(bankDetails);
        invoice.setSender(sender);
        var deliveryDateFrom = deliveryDate.withDayOfMonth(1);
        var deliveryDateTo = deliveryDate;
        invoice.setDetailedDeliveryPeriodFrom(fromLocalDate(deliveryDateFrom));
        invoice.setDetailedDeliveryPeriodTo(fromLocalDate(deliveryDateTo));
        invoice.setPaymentTermDescription("45 Tage netto");
        invoice.setDueDate(fromLocalDate(request.invoiceDate().plusDays(45)));
        request.items().stream().forEach(i -> {
            var item = new Item();
            item.setQuantity(BigDecimal.valueOf(i.quantity()));
            item.setId(String.valueOf(i.itemId()));
            item.setPrice(BigDecimal.valueOf(i.unitNetAmount()));
            item.setLineTotalAmount(BigDecimal.valueOf(i.totalNetAmount()));
            item.setBasisQuantity(BigDecimal.ONE);
            item.setGrossPrice(BigDecimal.valueOf(i.totalNetAmount() * request.vatPercentage() / 100));
            var product = new Product();
            product.setName(i.description());
            // product.setDescription(i.description());
            product.setVATPercent(BigDecimal.valueOf(request.vatPercentage()));
            product.setUnit("C62");
            item.setProduct(product);
            invoice.addItem(item);
        });
        invoice.addCustomsNote(request.invoiceText());
        invoice.setContractReferencedDocument(String.valueOf(request.referenceNumber()));
        return invoice;
    }

    private String buildXInvoiceFilePath(String filePath) {
        var parts = filePath.split("\\.");
        return parts[0] + "_X." + parts[1];
    }


}
