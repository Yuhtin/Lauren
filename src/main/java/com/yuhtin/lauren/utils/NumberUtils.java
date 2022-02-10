package com.yuhtin.lauren.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NumberUtils {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    public static String format(double valor) {
        return DECIMAL_FORMAT.format(valor);
    }

    public static Number createNumber(String str) throws NumberFormatException {

        if (str == null) return null;
        if (isEmpty(str)) throw new NumberFormatException("A blank string is not a valid number");
        if (str.startsWith("--")) return null;

        if (!str.startsWith("0x") && !str.startsWith("-0x")) {
            char lastChar = str.charAt(str.length() - 1);
            int decPos = str.indexOf(46);
            int expPos = str.indexOf(101) + str.indexOf(69) + 1;
            String dec;
            String mant;
            if (decPos <= -1) {
                if (expPos > -1) {
                    if (expPos > str.length()) {
                        throw new NumberFormatException(str + " is not a valid number.");
                    }

                    mant = str.substring(0, expPos);
                } else {
                    mant = str;
                }

                dec = null;
            } else {
                if (expPos > -1) {
                    if (expPos < decPos || expPos > str.length()) {
                        throw new NumberFormatException(str + " is not a valid number.");
                    }

                    dec = str.substring(decPos + 1, expPos);
                } else {
                    dec = str.substring(decPos + 1);
                }

                mant = str.substring(0, decPos);
            }

            String exp;
            if (!Character.isDigit(lastChar) && lastChar != '.') {
                if (expPos > -1 && expPos < str.length() - 1) {
                    exp = str.substring(expPos + 1, str.length() - 1);
                } else {
                    exp = null;
                }

                String numeric = str.substring(0, str.length() - 1);
                boolean allZeros = isAllZeros(mant) && isAllZeros(exp);
                switch (lastChar) {
                    case 'D':
                    case 'd':
                        break;
                    case 'F':
                    case 'f':
                        try {
                            Float f = createFloat(numeric);
                            if (f.isInfinite() || f == 0.0F && !allZeros) {
                                break;
                            }

                            return f;
                        } catch (NumberFormatException var15) {
                            break;
                        }
                    case 'L':
                    case 'l':
                        if (dec == null && exp == null && (numeric.charAt(0) == '-' && isDigits(numeric.substring(1)) || isDigits(numeric))) {
                            try {
                                return createLong(numeric);
                            } catch (NumberFormatException var11) {
                                return createBigInteger(numeric);
                            }
                        } else {
                            throw new NumberFormatException(str + " is not a valid number.");
                        }
                    default:
                        throw new NumberFormatException(str + " is not a valid number.");
                }

                try {
                    Double d = createDouble(numeric);
                    if (!d.isInfinite() && ((double) d.floatValue() != 0.0D || allZeros)) {
                        return d;
                    }
                } catch (NumberFormatException var14) {
                }

                try {
                    return createBigDecimal(numeric);
                } catch (NumberFormatException var13) {
                    throw new NumberFormatException(str + " is not a valid number.");
                }
            } else {
                if (expPos > -1 && expPos < str.length() - 1) {
                    exp = str.substring(expPos + 1);
                } else {
                    exp = null;
                }

                if (dec == null && exp == null) {
                    try {
                        return createInteger(str);
                    } catch (NumberFormatException var12) {
                        try {
                            return createLong(str);
                        } catch (NumberFormatException var10) {
                            return createBigInteger(str);
                        }
                    }
                } else {
                    boolean allZeros = isAllZeros(mant) && isAllZeros(exp);

                    try {
                        Float f = createFloat(str);
                        if (!f.isInfinite() && (f != 0.0F || allZeros)) {
                            return f;
                        }
                    } catch (NumberFormatException var17) {
                    }

                    try {
                        Double d = createDouble(str);
                        if (!d.isInfinite() && (d != 0.0D || allZeros)) {
                            return d;
                        }
                    } catch (NumberFormatException var16) {
                    }

                    return createBigDecimal(str);
                }
            }
        } else {
            return createInteger(str);
        }
    }

    private static boolean isAllZeros(String str) {
        if (str == null) {
            return true;
        } else {
            for (int i = str.length() - 1; i >= 0; --i) {
                if (str.charAt(i) != '0') {
                    return false;
                }
            }

            return str.length() > 0;
        }
    }

    public static Float createFloat(String str) {
        return str == null ? null : Float.valueOf(str);
    }

    public static Double createDouble(String str) {
        return str == null ? null : Double.valueOf(str);
    }

    public static Integer createInteger(String str) {
        return str == null ? null : Integer.decode(str);
    }

    public static Long createLong(String str) {
        return str == null ? null : Long.valueOf(str);
    }

    public static BigInteger createBigInteger(String str) {
        return str == null ? null : new BigInteger(str);
    }

    public static BigDecimal createBigDecimal(String str) {

        if (str == null) return null;
        if (str.equalsIgnoreCase("")) throw new NumberFormatException("A blank string is not a valid number");
        else return new BigDecimal(str);

    }

    public static boolean isDigits(String str) {

        if (isEmpty(str)) return false;

        for (int i = 0; i < str.length(); ++i) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }

        return true;

    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

}
