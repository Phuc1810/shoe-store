package com.group2.shoestore.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public final class MoneyUtil {

    private static final Locale VIETNAM = new Locale("vi", "VN");

    private MoneyUtil() {
    }

    public static String formatVnd(BigDecimal amount) {
        if (amount == null) {
            return NumberFormat.getCurrencyInstance(VIETNAM).format(BigDecimal.ZERO);
        }
        return NumberFormat.getCurrencyInstance(VIETNAM).format(amount);
    }
}
