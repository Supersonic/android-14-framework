package com.android.internal.util;

import android.media.MediaMetrics;
import android.text.format.DateFormat;
import com.android.internal.midi.MidiConstants;
/* loaded from: classes3.dex */
public class HexDump {
    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', DateFormat.CAPITAL_AM_PM, 'B', 'C', 'D', DateFormat.DAY, 'F'};
    private static final char[] HEX_LOWER_CASE_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', DateFormat.AM_PM, 'b', 'c', DateFormat.DATE, 'e', 'f'};

    public static String dumpHexString(byte[] array) {
        return array == null ? "(null)" : dumpHexString(array, 0, array.length);
    }

    public static String dumpHexString(byte[] array, int offset, int length) {
        if (array == null) {
            return "(null)";
        }
        StringBuilder result = new StringBuilder();
        byte[] line = new byte[16];
        int lineIndex = 0;
        result.append("\n0x");
        result.append(toHexString(offset));
        int i = offset;
        while (i < offset + length) {
            if (lineIndex == 16) {
                result.append(" ");
                for (int j = 0; j < 16; j++) {
                    if (line[j] > 32 && line[j] < 126) {
                        result.append(new String(line, j, 1));
                    } else {
                        result.append(MediaMetrics.SEPARATOR);
                    }
                }
                result.append("\n0x");
                result.append(toHexString(i));
                lineIndex = 0;
            }
            byte b = array[i];
            result.append(" ");
            char[] cArr = HEX_DIGITS;
            result.append(cArr[(b >>> 4) & 15]);
            result.append(cArr[b & MidiConstants.STATUS_CHANNEL_MASK]);
            line[lineIndex] = b;
            i++;
            lineIndex++;
        }
        if (lineIndex != 16) {
            int count = (16 - lineIndex) * 3;
            int count2 = count + 1;
            for (int i2 = 0; i2 < count2; i2++) {
                result.append(" ");
            }
            for (int i3 = 0; i3 < lineIndex; i3++) {
                if (line[i3] > 32 && line[i3] < 126) {
                    result.append(new String(line, i3, 1));
                } else {
                    result.append(MediaMetrics.SEPARATOR);
                }
            }
        }
        return result.toString();
    }

    public static String toHexString(byte b) {
        return toHexString(toByteArray(b));
    }

    public static String toHexString(byte[] array) {
        return toHexString(array, 0, array.length, true);
    }

    public static String toHexString(byte[] array, boolean upperCase) {
        return toHexString(array, 0, array.length, upperCase);
    }

    public static String toHexString(byte[] array, int offset, int length) {
        return toHexString(array, offset, length, true);
    }

    public static String toHexString(byte[] array, int offset, int length, boolean upperCase) {
        char[] digits = upperCase ? HEX_DIGITS : HEX_LOWER_CASE_DIGITS;
        char[] buf = new char[length * 2];
        int bufIndex = 0;
        for (int i = offset; i < offset + length; i++) {
            byte b = array[i];
            int bufIndex2 = bufIndex + 1;
            buf[bufIndex] = digits[(b >>> 4) & 15];
            bufIndex = bufIndex2 + 1;
            buf[bufIndex2] = digits[b & MidiConstants.STATUS_CHANNEL_MASK];
        }
        return new String(buf);
    }

    public static String toHexString(int i) {
        return toHexString(toByteArray(i));
    }

    public static byte[] toByteArray(byte b) {
        byte[] array = {b};
        return array;
    }

    public static byte[] toByteArray(int i) {
        byte[] array = {(byte) ((i >> 24) & 255), (byte) ((i >> 16) & 255), (byte) ((i >> 8) & 255), (byte) (i & 255)};
        return array;
    }

    private static int toByte(char c) {
        if (c < '0' || c > '9') {
            if (c < 'A' || c > 'F') {
                if (c < 'a' || c > 'f') {
                    throw new RuntimeException("Invalid hex char '" + c + "'");
                }
                return (c - 'a') + 10;
            }
            return (c - 'A') + 10;
        }
        return c - '0';
    }

    public static byte[] hexStringToByteArray(String hexString) {
        int length = hexString.length();
        byte[] buffer = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            buffer[i / 2] = (byte) ((toByte(hexString.charAt(i)) << 4) | toByte(hexString.charAt(i + 1)));
        }
        return buffer;
    }

    public static StringBuilder appendByteAsHex(StringBuilder sb, byte b, boolean upperCase) {
        char[] digits = upperCase ? HEX_DIGITS : HEX_LOWER_CASE_DIGITS;
        sb.append(digits[(b >> 4) & 15]);
        sb.append(digits[b & MidiConstants.STATUS_CHANNEL_MASK]);
        return sb;
    }
}
