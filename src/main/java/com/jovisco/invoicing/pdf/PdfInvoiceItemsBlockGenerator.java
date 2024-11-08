package com.jovisco.invoicing.pdf;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.util.Map;

public class PdfInvoiceItemsBlockGenerator {

    protected static final int[] TEXT_COLOR = {0, 0, 0};
    protected final PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

    protected final RequestMap requestMap;
    protected final PDPageContentStream cs;
    protected final float startY;

    private float posY;

    public PdfInvoiceItemsBlockGenerator(RequestMap requestMap, PDPageContentStream cs, float startY) {
        this.requestMap = requestMap;
        this.cs = cs;
        this.startY = startY;
    }

    public PdfElement generate() {
        posY = startY;
        PdfElement[] items = new PdfElement[requestMap.getItems().size()];
        for (int i = 0; i < requestMap.getItems().size(); i++) {
            items[i] = generateItem(requestMap.getItems().get(i));
            posY += 7.0f;
        }
        return new PdfBlock(items);
    }

    protected PdfElement generateItem(Map<String, String> itemMap) {
        return new PdfBlock(
                generateItemId(itemMap.get(RequestMap.ITEM_ID)),
                generateItemQuantity(itemMap.get(RequestMap.ITEM_QTY)),
                generateItemDescription(itemMap.get(RequestMap.ITEM_DESC)),
                generateItemUnitNetAmount(itemMap.get(RequestMap.ITEM_UNIT_NET_AMNT)),
                generateItemTotalNetAmount(itemMap.get(RequestMap.ITEM_TOTAL_NET_AMNT))
        );
    }

    protected PdfElement generateItemId(String itemId) {
        var itemX = 28.0f - (itemId.length() * 1.0f) + 1;
        return PdfText.builder()
                .contentStream(cs)
                .font(font)
                .colorRGB(TEXT_COLOR)
                .text(itemId)
                .dimensions(PdfDimensions.ofA4mm(itemX, posY, 5.0f, 12.0f))
                .build();
    }

    protected PdfElement generateItemQuantity(String quantity) {
        var itemX = 47.0f - (quantity.length() * 2.0f) + 1;
        return PdfText.builder()
                .contentStream(cs)
                .font(font)
                .colorRGB(TEXT_COLOR)
                .text(quantity)
                .dimensions(PdfDimensions.ofA4mm(itemX, posY, 10.0f, 12.0f))
                .build();
    }

    protected PdfElement generateItemDescription(String description) {
        var fontSize = 12.0f - (float) (description.length() / 35);
        return PdfText.builder()
                .contentStream(cs)
                .font(font)
                .colorRGB(TEXT_COLOR)
                .text(description)
                .dimensions(PdfDimensions.ofA4mm(58.0f, posY, 80.0f, fontSize))
                .build();
    }

    protected PdfElement generateItemUnitNetAmount(String amount) {
        var itemX = 155.0f - (amount.length() * 2.1f) + 1;
        return PdfText.builder()
                .contentStream(cs)
                .font(font)
                .colorRGB(TEXT_COLOR)
                .text(amount)
                .dimensions(PdfDimensions.ofA4mm(itemX, posY, 20.0f, 12.0f))
                .build();
    }

    protected PdfElement generateItemTotalNetAmount(String amount) {
        var itemX = 185.0f - (amount.length() * 1.8f) + 1;
        return PdfText.builder()
                .contentStream(cs)
                .font(font)
                .colorRGB(TEXT_COLOR)
                .text(amount)
                .dimensions(PdfDimensions.ofA4mm(itemX, posY, 25.0f, 12.0f))
                .build();
    }

    protected float getPosY() {
        return posY;
    }
}
