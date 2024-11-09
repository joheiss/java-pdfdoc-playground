package com.jovisco.invoicing.pdf;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

public class PdfInvoiceItemsTotalsBlockGenerator extends PdfInvoiceBlockGenerator {

    public PdfInvoiceItemsTotalsBlockGenerator(RequestMap requestMap, PDPageContentStream cs, float startY) {
        super(requestMap, cs, startY);
    }

    @Override
    public PdfElement generate() {
        posY = startY;
        var header = generateItemsTotalsHeader();
        posY += 5.0f;
        var amounts = generateItemsTotalAmounts();
        posY += 5.0f;
        var line = generateLine(PdfDimensions.ofA4mm(26.0f, posY, LINE_WIDTH, 1.0f));

        return new PdfBlock(header, amounts, line);
    }

    protected PdfElement generateItemsTotalsHeader() {
        var topLine = generateLine(PdfDimensions.ofA4mm(26.0f, posY, LINE_WIDTH, 1.0f));
        posY += 2.5f;
        var itemsTotalsHeaderTexts = generateItemsTotalsHeaderTexts();
        posY += 3.0f;
        var bottomLine = generateLine(PdfDimensions.ofA4mm(26.0f, posY, LINE_WIDTH, 1.0f));

        return new PdfBlock(topLine, itemsTotalsHeaderTexts, bottomLine);
    }

    protected PdfElement generateItemsTotalsHeaderTexts() {
        return new PdfBlock(
                generateTotalNetAmountHeader(),
                generateTotalVatAmountHeader(),
                generateTotalGrossAmountHeader()
        );
    }

    private PdfElement generateTotalNetAmountHeader() {
        return PdfText.builder()
                .contentStream(cs)
                .dimensions(PdfDimensions.ofA4mm(40.0f, posY, 20.0f, 9.0f))
                .text(requestMap.get(RequestMap.TOTAL_NET_AMNT_HDR))
                .font(font)
                .colorRGB(TEMPLATE_COLOR)
                .build();
    }

    private PdfElement generateTotalVatAmountHeader() {
        return PdfText.builder()
                .contentStream(cs)
                .dimensions(PdfDimensions.ofA4mm(96.5f, posY, 20.0f, 9.0f))
                .text(requestMap.get(RequestMap.TOTAL_VAT_AMNT_HDR))
                .font(font)
                .colorRGB(TEMPLATE_COLOR)
                .build();
    }

    private PdfElement generateTotalGrossAmountHeader() {
        return PdfText.builder()
                .contentStream(cs)
                .dimensions(PdfDimensions.ofA4mm(172.0f, posY, 20.0f, 9.0f))
                .text(requestMap.get(RequestMap.TOTAL_GROSS_AMNT_HDR))
                .font(font)
                .colorRGB(TEMPLATE_COLOR)
                .build();
    }

    protected PdfElement generateItemsTotalAmounts() {
        return new PdfBlock(
                generateTotalNetAmount(),
                generateTotalVatAmount(),
                generateTotalGrossAmount()
        );

    }

    protected PdfElement generateTotalNetAmount() {
        var totalNetAmount = requestMap.get(RequestMap.TOTAL_NET_AMNT);
        var posX = 51.5f - (totalNetAmount.length() * 1.8f) + 1;

        return PdfText.builder()
                .contentStream(cs)
                .font(font)
                .colorRGB(TEXT_COLOR)
                .text(totalNetAmount)
                .dimensions(PdfDimensions.ofA4mm(posX, posY, 25.0f, 12.0f))
                .build();
    }

    protected PdfElement generateTotalVatAmount() {
        var totalVatAmount = requestMap.get(RequestMap.TOTAL_VAT_AMNT);
        var posX = 108.0f - (totalVatAmount.length() * 1.8f) + 1;

        return PdfText.builder()
                .contentStream(cs)
                .font(font)
                .colorRGB(TEXT_COLOR)
                .text(totalVatAmount)
                .dimensions(PdfDimensions.ofA4mm(posX, posY, 25.0f, 12.0f))
                .build();
    }

    protected PdfElement generateTotalGrossAmount() {
        var totalGrossAmount = requestMap.get(RequestMap.TOTAL_GROSS_AMNT);
        var posX = 185.0f - (totalGrossAmount.length() * 1.8f) + 1;

        return PdfText.builder()
                .contentStream(cs)
                .font(font)
                .colorRGB(TEXT_COLOR)
                .text(totalGrossAmount)
                .dimensions(PdfDimensions.ofA4mm(posX, posY, 25.0f, 12.0f))
                .build();
    }
}
