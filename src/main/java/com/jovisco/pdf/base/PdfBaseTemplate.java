package com.jovisco.pdf.base;

import com.jovisco.pdf.core.PdfDocumentCreator;
import com.jovisco.pdf.core.PdfDocumentException;
import com.jovisco.pdf.core.PdfDocumentGenerator;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

@RequiredArgsConstructor
public class PdfBaseTemplate implements PdfDocumentCreator {

    private final PdfDocumentGenerator generator;
    private final String filePath;

    public void create() {
        try (var template = new PDDocument()) {
            var page = new PDPage(PDRectangle.A4);
            template.addPage(page);
            // load the font from pdfbox.jar
            InputStream fontStream = PdfBaseTemplate.class.getResourceAsStream("/ArialMT.ttf");
            PDFont font = PDType0Font.load(template, fontStream);
            generator.generate(template);
            fillMetaInformation(template);
            template.save(filePath);
        } catch (IOException e) {
            throw new PdfDocumentException(e.getMessage());
        }
    }

    private void fillMetaInformation(PDDocument doc) {

        var now = Calendar.getInstance();

        var metadata = doc.getDocumentInformation();
        metadata.setTitle("Jovisco GmbH - Base Template");
        metadata.setAuthor("Jo Heiss");
        metadata.setSubject("Base Template");
        metadata.setCreationDate(now);
        metadata.setModificationDate(now);
        metadata.setKeywords("Jovisco, Template ");
        metadata.setProducer("PDFBox");
    }
}
