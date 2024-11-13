package com.jovisco.pdf.shared;

import com.jovisco.pdf.core.RequestMap;
import lombok.RequiredArgsConstructor;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RequiredArgsConstructor
public class PdfInvoiceRequestAdapter_deDE {

    private final Locale locale;

    public RequestMap map(CreatePdfInvoiceRequest request) {
        var bundle = getResourceBundle();
        var requestMap = new HashMap<String, Object>();
        requestMap.put(RequestMap.ADDRESS_LINES, request.address());
        requestMap.put(RequestMap.TITLE, bundle.getString(RequestMap.TITLE));
        requestMap.put(RequestMap.REF_TITLE, bundle.getString(RequestMap.REF_TITLE));
        requestMap.put(RequestMap.CUSTOMER_ID_LBL, bundle.getString(RequestMap.CUSTOMER_ID_LBL));
        requestMap.put(RequestMap.CUSTOMER_ID, Integer.toString(request.customerId()));
        requestMap.put(RequestMap.INVOICE_ID_LBL, bundle.getString(RequestMap.INVOICE_ID_LBL));
        requestMap.put(RequestMap.INVOICE_ID, Integer.toString(request.invoiceId()));
        requestMap.put(RequestMap.INVOICE_DATE_LBL, bundle.getString(RequestMap.INVOICE_DATE_LBL));
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", locale);
        requestMap.put(RequestMap.INVOICE_DATE, request.invoiceDate().format(dateFormatter));
        requestMap.put(RequestMap.BILLING_PERIOD, request.billingPeriod());
        requestMap.put(RequestMap.PAYMENT_TERMS, request.paymentTerms());
        var formatter = NumberFormat.getCurrencyInstance();
        requestMap.put(RequestMap.TOTAL_NET_AMNT, formatter.format(request.totalNetAmount()));
        requestMap.put(RequestMap.TOTAL_VAT_AMNT, formatter.format(request.totalVatAmount()));
        requestMap.put(RequestMap.TOTAL_GROSS_AMNT, formatter.format(request.totalGrossAmount()));
        requestMap.put(RequestMap.ITEM_ID_HDR, bundle.getString(RequestMap.ITEM_ID_HDR));
        requestMap.put(RequestMap.ITEM_QTY_HDR, bundle.getString(RequestMap.ITEM_QTY_HDR));
        requestMap.put(RequestMap.ITEM_DESC_HDR, bundle.getString(RequestMap.ITEM_DESC_HDR));
        requestMap.put(RequestMap.ITEM_UNIT_NET_AMNT_HDR, bundle.getString(RequestMap.ITEM_UNIT_NET_AMNT_HDR));
        requestMap.put(RequestMap.ITEM_TOTAL_NET_AMNT_HDR, bundle.getString(RequestMap.ITEM_TOTAL_NET_AMNT_HDR));
        requestMap.put(RequestMap.TOTAL_NET_AMNT_HDR, bundle.getString(RequestMap.TOTAL_NET_AMNT_HDR));
        DecimalFormat df = new DecimalFormat("0.##");
        var vatPercentage = df.format(request.vatPercentage());
        requestMap.put(RequestMap.TOTAL_VAT_AMNT_HDR,
                vatPercentage + bundle.getString(RequestMap.TOTAL_VAT_AMNT_HDR));
        requestMap.put(RequestMap.TOTAL_GROSS_AMNT_HDR, bundle.getString(RequestMap.TOTAL_GROSS_AMNT_HDR));

        var items = new ArrayList<Map<String, String>>();
        for (var item : request.items()) {
            var itemMap = new HashMap<String, String>();
            itemMap.put(RequestMap.ITEM_ID, Integer.toString(item.itemId()));
            itemMap.put(RequestMap.ITEM_QTY, df.format(item.quantity()));
            itemMap.put(RequestMap.ITEM_DESC, item.description());
            itemMap.put(RequestMap.ITEM_UNIT_NET_AMNT, formatter.format(item.unitNetAmount()));
            itemMap.put(RequestMap.ITEM_TOTAL_NET_AMNT, formatter.format(item.unitNetAmount()));
            items.add(itemMap);
        }
        requestMap.put(RequestMap.ITEMS, List.copyOf(items));
        return new RequestMap(requestMap);
    }

    private ResourceBundle getResourceBundle() {
        return ResourceBundle.getBundle("i18n", locale);
    }
}
