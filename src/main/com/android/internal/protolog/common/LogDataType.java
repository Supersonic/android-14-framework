package com.android.internal.protolog.common;

import java.util.ArrayList;
import java.util.List;
/* loaded from: classes4.dex */
public class LogDataType {
    public static final int BOOLEAN = 3;
    public static final int DOUBLE = 2;
    public static final int LONG = 1;
    public static final int STRING = 0;
    private static final int TYPE_MASK = 3;
    private static final int TYPE_WIDTH = 2;

    public static int logDataTypesToBitMask(List<Integer> types) {
        if (types.size() > 16) {
            throw new BitmaskConversionException("Too many log call parameters - max 16 parameters supported");
        }
        int mask = 0;
        for (int i = 0; i < types.size(); i++) {
            int x = types.get(i).intValue();
            mask |= x << (i * 2);
        }
        return mask;
    }

    public static int bitmaskToLogDataType(int bitmask, int index) {
        if (index > 16) {
            throw new BitmaskConversionException("Max 16 parameters allowed");
        }
        return (bitmask >> (index * 2)) & 3;
    }

    public static List<Integer> parseFormatString(String messageString) {
        ArrayList<Integer> types = new ArrayList<>();
        int i = 0;
        while (i < messageString.length()) {
            if (messageString.charAt(i) == '%') {
                if (i + 1 >= messageString.length()) {
                    throw new InvalidFormatStringException("Invalid format string in config");
                }
                switch (messageString.charAt(i + 1)) {
                    case '%':
                        break;
                    case 'b':
                        types.add(3);
                        break;
                    case 'd':
                    case 'x':
                        types.add(1);
                        break;
                    case 'f':
                        types.add(2);
                        break;
                    case 's':
                        types.add(0);
                        break;
                    default:
                        throw new InvalidFormatStringException("Invalid format string field %${messageString[i + 1]}");
                }
                i += 2;
            } else {
                i++;
            }
        }
        return types;
    }
}
