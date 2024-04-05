package com.yuhtin.lauren.util;


import lombok.NoArgsConstructor;

import java.text.DecimalFormat;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class NumberUtil {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#0");

    public static String format(double number) {
        return DECIMAL_FORMAT.format(number);
    }

    public static String formatToPercentage(double number) {
        return DECIMAL_FORMAT.format((number - 1) * 100) + "%";
    }

    public static double parsePercentage(String profileMultiplier) {
        return Double.parseDouble(profileMultiplier.replace("%", "")) / 100 + 1;
    }

    public static double parse(String actionValue) {
        return Double.parseDouble(actionValue);
    }
}
