package com.jovisco.pdf.invoice;

import com.jovisco.pdf.core.*;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

public class PdfInvoiceItemsHeaderBlockGenerator extends PdfBlockGenerator {

    public PdfInvoiceItemsHeaderBlockGenerator(RequestMap requestMap, PDPageContentStream cs, PdfPosY posY) {
        super(requestMap, cs, posY);
    }

    @Override
    public PdfElement generate() {
        var topLine = generateLine(PdfDimensions.ofA4mm(26.0f, posY.getY(), LINE_WIDTH, 1.0f));
        posY.incrementBy(2.5f);
        var header = generateItemsColumnsHeaderTexts();
        posY.incrementBy(3.5f);
        var bottomLine = generateLine(PdfDimensions.ofA4mm(26.0f, posY.getY(), LINE_WIDTH, 1.0f));

        return new PdfBlock(topLine, header, bottomLine);
    }

    protected PdfElement generateItemsColumnsHeaderTexts() {
        return new PdfBlock(
                generateItemIdHeader(),
                generateItemQuantityHeader(),
                generateItemDescriptionHeader(),
                generateItemUnitNetAmountHeader(),
                generateItemTotalNetAmountHeader()
        );
    }

    private PdfElement generateItemIdHeader() {
        return PdfText.builder()
                .contentStream(cs)
                .dimensions(PdfDimensions.ofA4mm(26.5f, posY.getY(), 10.0f, 9.0f))
                .font(font)
                .colorRGB(TEMPLATE_COLOR)
                .text(requestMap.get(RequestMap.ITEM_ID_HDR))
                .build();
    }

    private PdfElement generateItemQuantityHeader() {
        return PdfText.builder()
                .contentStream(cs)
                .dimensions(PdfDimensions.ofA4mm(38.5f, posY.getY(), 10.0f, 9.0f))
                .font(font)
                .colorRGB(TEMPLATE_COLOR)
                .text(requestMap.get(RequestMap.ITEM_QTY_HDR))
                .build();
    }

    private PdfElement generateItemDescriptionHeader() {
        return PdfText.builder()
                .contentStream(cs)
                .dimensions(PdfDimensions.ofA4mm(58.0f, posY.getY(), 10.0f, 9.0f))
                .font(font)
                .colorRGB(TEMPLATE_COLOR)
                .text(requestMap.get(RequestMap.ITEM_DESC_HDR))
                .build();
    }

    private PdfElement generateItemUnitNetAmountHeader() {
        return PdfText.builder()
                .contentStream(cs)
                .dimensions(PdfDimensions.ofA4mm(141.0f, posY.getY(), 10.0f, 9.0f))
                .font(font)
                .colorRGB(TEMPLATE_COLOR)
                .text(requestMap.get(RequestMap.ITEM_UNIT_NET_AMNT_HDR))
                .build();
    }

    private PdfElement generateItemTotalNetAmountHeader() {
        return PdfText.builder()
                .contentStream(cs)
                .dimensions(PdfDimensions.ofA4mm(171.0f, posY.getY(), 10.0f, 9.0f))
                .font(font)
                .colorRGB(TEMPLATE_COLOR)
                .text(requestMap.get(RequestMap.ITEM_TOTAL_NET_AMNT_HDR))
                .build();
    }
}
