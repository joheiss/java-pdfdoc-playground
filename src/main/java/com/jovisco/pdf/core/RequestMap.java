package com.jovisco.pdf.core;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RequestMap {

    // invoice template
    public static final String TITLE = "title";
    public static final String REF_TITLE = "referenceTitle";
    public static final String CUSTOMER_ID_LBL = "customerIdLabel";
    public static final String INVOICE_ID_LBL = "invoiceIdLabel";
    public static final String INVOICE_DATE_LBL = "invoiceDateLabel";
    public static final String ITEM_ID_HDR = "itemIdHeader";
    public static final String ITEM_QTY_HDR = "itemQuantityHeader";
    public static final String ITEM_DESC_HDR= "itemDescriptionHeader";
    public static final String ITEM_UNIT_NET_AMNT_HDR= "itemUnitNetAmountHeader";
    public static final String ITEM_TOTAL_NET_AMNT_HDR= "itemTotalNetAmountHeader";

    // invoice document
    public static final String CURRENCY_CODE = "currencyCode";
    public static final String INVOICE_ID = "invoiceId";
    public static final String CUSTOMER_ID = "customerId";
    public static final String INVOICE_DATE = "invoiceDate";
    public static final String ADDRESS_LINES = "addressLines";
    public static final String BILLING_PERIOD = "billingPeriod";
    public static final String ITEMS = "items";
    public static final String TOTAL_NET_AMNT_HDR = "totalNetAmountHeader";
    public static final String TOTAL_VAT_AMNT_HDR = "totalVatAmountHeader";
    public static final String TOTAL_GROSS_AMNT_HDR = "totalGrossAmountHeader";
    public static final String TOTAL_NET_AMNT = "totalNetAmount";
    public static final String TOTAL_VAT_AMNT = "totalVatAmount";
    public static final String TOTAL_GROSS_AMNT = "totalGrossAmount";
    public static final String PAYMENT_TERMS = "paymentTerms";
    public static final String OPT_INVOICE_TEXTS = "optionalInvoiceTexts";

    // invoice item
    public static final String ITEM_ID = "itemId";
    public static final String ITEM_QTY = "quantity";
    public static final String ITEM_DESC = "description";
    public static final String ITEM_UNIT_NET_AMNT = "unitNetAmount";
    public static final String ITEM_TOTAL_NET_AMNT = "totalNetAmount";

    private final Map<String, Object> requestMap;

    public RequestMap(Map<String, Object> requestMap) {
        this.requestMap = requestMap;
    }

    public String get(String key) {
        return String.valueOf(requestMap.get(key) != null ? requestMap.get(key) : "");
    }

    public List<String> getList(String key) {
        if (requestMap.get(key) instanceof List<?> list) {
            return !list.isEmpty()
                    ? list.stream()
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .toList()
                    : List.of();
        }
        return List.of();
    }

    public List<Map<String, String>> getItems() {
        if (requestMap.get(ITEMS) instanceof List<?> items) {
            return !items.isEmpty()
                    ? items.stream()
                    .filter(Objects::nonNull)
                    .map(obj -> (Map<String, String>) obj)
                    .toList()
                    : List.of();
        }
        return List.of();
    }
}
