package com.android.internal.util;
/* loaded from: classes3.dex */
public final class ParseUtils {
    private ParseUtils() {
    }

    public static int parseInt(String value, int defValue) {
        return parseIntWithBase(value, 10, defValue);
    }

    public static int parseIntWithBase(String value, int base, int defValue) {
        if (value == null) {
            return defValue;
        }
        try {
            return Integer.parseInt(value, base);
        } catch (NumberFormatException e) {
            return defValue;
        }
    }

    public static long parseLong(String value, long defValue) {
        return parseLongWithBase(value, 10, defValue);
    }

    public static long parseLongWithBase(String value, int base, long defValue) {
        if (value == null) {
            return defValue;
        }
        try {
            return Long.parseLong(value, base);
        } catch (NumberFormatException e) {
            return defValue;
        }
    }

    public static float parseFloat(String value, float defValue) {
        if (value == null) {
            return defValue;
        }
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return defValue;
        }
    }

    public static double parseDouble(String value, double defValue) {
        if (value == null) {
            return defValue;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return defValue;
        }
    }

    public static boolean parseBoolean(String value, boolean defValue) {
        if ("true".equals(value)) {
            return true;
        }
        return ("false".equals(value) || parseInt(value, defValue ? 1 : 0) == 0) ? false : true;
    }
}
