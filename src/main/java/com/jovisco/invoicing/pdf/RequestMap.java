package com.jovisco.invoicing.pdf;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RequestMap {

    // invoice template
    static final String TITLE = "title";
    static final String REF_TITLE = "referenceTitle";
    static final String CUSTOMER_ID_LBL = "customerIdLabel";
    static final String INVOICE_ID_LBL = "invoiceIdLabel";
    static final String INVOICE_DATE_LBL = "invoiceDateLabel";
    static final String ITEM_ID_HDR = "itemIdHeader";
    static final String ITEM_QTY_HDR = "itemQuantityHeader";
    static final String ITEM_DESC_HDR= "itemDescriptionHeader";
    static final String ITEM_UNIT_NET_AMNT_HDR= "itemUnitNetAmountHeader";
    static final String ITEM_TOTAL_NET_AMNT_HDR= "itemTotalNetAmountHeader";

    // invoice document
    static final String INVOICE_ID = "invoiceId";
    static final String CUSTOMER_ID = "customerId";
    static final String INVOICE_DATE = "invoiceDate";
    static final String ADDRESS_LINES = "addressLines";
    static final String BILLING_PERIOD = "billingPeriod";
    static final String ITEMS = "items";
    static final String TOTAL_NET_AMNT_HDR = "totalNetAmountHeader";
    static final String TOTAL_VAT_AMNT_HDR = "totalVatAmountHeader";
    static final String TOTAL_GROSS_AMNT_HDR = "totalGrossAmountHeader";
    static final String TOTAL_NET_AMNT = "totalNetAmount";
    static final String TOTAL_VAT_AMNT = "totalVatAmount";
    static final String TOTAL_GROSS_AMNT = "totalGrossAmount";
    static final String PAYMENT_TERMS = "paymentTerms";
    static final String OPT_INVOICE_TEXTS = "optionalInvoiceTexts";

    // invoice item
    static final String ITEM_ID = "itemId";
    static final String ITEM_QTY = "quantity";
    static final String ITEM_DESC = "description";
    static final String ITEM_UNIT_NET_AMNT = "unitNetAmount";
    static final String ITEM_TOTAL_NET_AMNT = "totalNetAmount";

    private final Map<String, List<Object>> requestMap;

    public RequestMap(Map<String, List<Object>> requestMap) {
        this.requestMap = requestMap;
    }

    public String get(String key) {
        return String.valueOf(requestMap.get(key) != null ? requestMap.get(key).getFirst() : "");
    }

    public List<String> getList(String key) {
        return requestMap.get(key) != null
                ? requestMap.get(key).stream()
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .toList()
                : List.of();
    }

    public List<Map<String, String>> getItems() {
        return requestMap.get(ITEMS) != null
                ? requestMap.get(ITEMS).stream()
                    .filter(Objects::nonNull)
                    .map(obj -> (Map<String, String>) obj)
                    .toList()
                : List.of();
    }
}
