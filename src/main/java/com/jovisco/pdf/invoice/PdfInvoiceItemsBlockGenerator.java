package com.jovisco.pdf.invoice;

import com.jovisco.pdf.core.*;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.util.Map;

public class PdfInvoiceItemsBlockGenerator extends PdfBlockGenerator {

    public PdfInvoiceItemsBlockGenerator(RequestMap requestMap, PDPageContentStream cs, PDType0Font font,PdfPosY posY) {
        super(requestMap, cs, font, posY);
    }

    @Override
    public PdfElement generate() {
        PdfElement[] items = new PdfElement[requestMap.getItems().size()];
        for (int i = 0; i < requestMap.getItems().size(); i++) {
            items[i] = generateItem(requestMap.getItems().get(i));
            posY.incrementBy(7.0f);
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
                .dimensions(PdfDimensions.ofA4mm(itemX, posY.getY(), 5.0f, 11.0f))
                .build();
    }

    protected PdfElement generateItemQuantity(String quantity) {
        var itemX = 47.0f - (quantity.length() * 2.0f) + 1;
        return PdfText.builder()
                .contentStream(cs)
                .font(font)
                .colorRGB(TEXT_COLOR)
                .text(quantity)
                .dimensions(PdfDimensions.ofA4mm(itemX, posY.getY(), 10.0f, 11.0f))
                .build();
    }

    protected PdfElement generateItemDescription(String description) {
        var fontSize = 11.0f - (float) (description.length() / 35);
        return PdfText.builder()
                .contentStream(cs)
                .font(font)
                .colorRGB(TEXT_COLOR)
                .text(description)
                .dimensions(PdfDimensions.ofA4mm(58.0f, posY.getY(), 80.0f, fontSize))
                .build();
    }

    protected PdfElement generateItemUnitNetAmount(String amount) {
        var itemX = 155.0f - (amount.length() * 2.1f) + 1;
        return PdfText.builder()
                .contentStream(cs)
                .font(font)
                .colorRGB(TEXT_COLOR)
                .text(amount)
                .dimensions(PdfDimensions.ofA4mm(itemX, posY.getY(), 20.0f, 11.0f))
                .build();
    }

    protected PdfElement generateItemTotalNetAmount(String amount) {
        var itemX = 185.0f - (amount.length() * 1.8f) + 1;
        return PdfText.builder()
                .contentStream(cs)
                .font(font)
                .colorRGB(TEXT_COLOR)
                .text(amount)
                .dimensions(PdfDimensions.ofA4mm(itemX, posY.getY(), 25.0f, 11.0f))
                .build();
    }
}
