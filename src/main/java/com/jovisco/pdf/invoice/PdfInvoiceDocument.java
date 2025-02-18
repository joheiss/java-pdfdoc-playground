package com.jovisco.pdf.invoice;

import com.jovisco.pdf.core.PdfDocumentCreator;
import com.jovisco.pdf.core.PdfDocumentException;
import com.jovisco.pdf.core.PdfDocumentGenerator;
import com.jovisco.pdf.core.RequestMap;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.pdfbox.pdfwriter.compress.CompressParameters;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.color.PDOutputIntent;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.type.BadFieldValueException;
import org.apache.xmpbox.xml.XmpSerializer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Objects;

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
            createOutputIntent(document);
            document.save(new File(filePath), CompressParameters.NO_COMPRESSION);
            // document.save(filePath);
        } catch (IOException e) {
            throw new PdfDocumentException(e.getMessage());
        }
    }

    @SneakyThrows
    private void fillMetaInformation(PDDocument doc) {

        var now = Calendar.getInstance();
        var invoiceId = invoiceDocumentGenerator.getRequestMap().get(RequestMap.INVOICE_ID);

//        var metadata = doc.getDocumentInformation();
//        metadata.setTitle("Jovisco GmbH - Invoice " + invoiceId);
//        metadata.setAuthor("Jo Heiss");
//        metadata.setSubject("Invoice " + invoiceId);
//        metadata.setCreationDate(now);
//        metadata.setModificationDate(now);
//        metadata.setKeywords("Jovisco, Invoice, " + invoiceId);
//        metadata.setProducer("PDFBox");

        XMPMetadata xmp = XMPMetadata.createXMPMetadata();
        try {
            var dc = xmp.createAndAddDublinCoreSchema();
            dc.setTitle("Jovisco GmbH - Invoice " + invoiceId);

            var pdfAid = xmp.createAndAddPDFAIdentificationSchema();
            pdfAid.setConformance("B");
            pdfAid.setPart(1);

            XmpSerializer serializer = new XmpSerializer();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            serializer.serialize(xmp, baos, true);

            PDMetadata metadata = new PDMetadata(doc);
            metadata.importXMPMetadata(baos.toByteArray());
            doc.getDocumentCatalog().setMetadata(metadata);
        } catch (BadFieldValueException e) {
            throw new PdfDocumentException("Error while setting metadata: " + e.getMessage());
        }
    }

    @SneakyThrows
    private void createOutputIntent(PDDocument document) {
        // create output intent
//        @Cleanup var colorProfile =
//                PdfInvoiceDocument.class.getResourceAsStream("/sRGB_IEC61966-2-1_black_scaled.icc");
////        @Cleanup var colorProfile =
////                PdfInvoiceDocument.class.getResourceAsStream("/sRGB_IEC61966-2-1_no_black_scaling.icc");
//        PDOutputIntent oi = new PDOutputIntent(document, Objects.requireNonNull(colorProfile));
//        oi.setInfo("sRGB IEC61966-2.1");
//        oi.setOutputCondition("sRGB IEC61966-2.1");
//        oi.setOutputConditionIdentifier("sRGB IEC61966-2.1");
//        oi.setRegistryName("http://www.color.org");
//        return oi;

        @Cleanup InputStream colorProfile =  PdfInvoiceDocument.class.getResourceAsStream("/sRGB_IEC61966-2-1_black_scaled.icc");
        PDOutputIntent oi = new PDOutputIntent(document, Objects.requireNonNull(colorProfile));
        oi.setInfo("sRGB IEC61966-2.1");
        oi.setOutputCondition("sRGB IEC61966-2.1");
        oi.setOutputConditionIdentifier("sRGB IEC61966-2.1");
        oi.setRegistryName("http://www.color.org");
        document.getDocumentCatalog().addOutputIntent(oi);
        colorProfile.close();
    }
}
