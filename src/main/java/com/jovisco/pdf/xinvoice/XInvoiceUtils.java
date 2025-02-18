package com.jovisco.pdf.xinvoice;

import java.time.LocalDate;

public class XInvoiceUtils {

    public static java.util.Date fromLocalDate(LocalDate date) {
        return new java.util.Date(
                date.getYear() - 1900,
                date.getMonthValue() - 1,
                date.getDayOfMonth()
        );
    }
}
