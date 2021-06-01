package io.nkdtrdr.mrktmkr.utilities;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalUtilities {
    private BigDecimalUtilities() {

    }

    public static BigDecimal getBigDecimal(String stringValue) {
        return new BigDecimal(stringValue).setScale(8, RoundingMode.HALF_UP);
    }

    public static String formatString(BigDecimal d) {
        return d.toPlainString();
    }
}
