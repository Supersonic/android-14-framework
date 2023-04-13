package com.android.modules.utils;

import java.io.UTFDataFormatException;
/* loaded from: classes5.dex */
public class ModifiedUtf8 {
    public static String decode(byte[] in, char[] out, int offset, int utfSize) throws UTFDataFormatException {
        int count = 0;
        int s = 0;
        while (count < utfSize) {
            int count2 = count + 1;
            char c = (char) in[count + offset];
            out[s] = c;
            if (c < 128) {
                s++;
                count = count2;
            } else {
                char c2 = out[s];
                if ((c2 & 224) == 192) {
                    if (count2 >= utfSize) {
                        throw new UTFDataFormatException("bad second byte at " + count2);
                    }
                    int count3 = count2 + 1;
                    int b = in[count2 + offset];
                    if ((b & 192) != 128) {
                        throw new UTFDataFormatException("bad second byte at " + (count3 - 1));
                    }
                    out[s] = (char) (((c2 & 31) << 6) | (b & 63));
                    s++;
                    count = count3;
                } else if ((c2 & 240) == 224) {
                    if (count2 + 1 >= utfSize) {
                        throw new UTFDataFormatException("bad third byte at " + (count2 + 1));
                    }
                    int count4 = count2 + 1;
                    int b2 = in[count2 + offset];
                    int count5 = count4 + 1;
                    int c3 = in[count4 + offset];
                    if ((b2 & 192) != 128 || (c3 & 192) != 128) {
                        throw new UTFDataFormatException("bad second or third byte at " + (count5 - 2));
                    }
                    out[s] = (char) (((c2 & 15) << 12) | ((b2 & 63) << 6) | (c3 & 63));
                    s++;
                    count = count5;
                } else {
                    throw new UTFDataFormatException("bad byte at " + (count2 - 1));
                }
            }
        }
        return new String(out, 0, s);
    }

    public static long countBytes(String s, boolean shortLength) throws UTFDataFormatException {
        long result = 0;
        int length = s.length();
        for (int i = 0; i < length; i++) {
            char ch = s.charAt(i);
            if (ch != 0 && ch <= 127) {
                result++;
            } else if (ch <= 2047) {
                result += 2;
            } else {
                result += 3;
            }
            if (shortLength && result > 65535) {
                throw new UTFDataFormatException("String more than 65535 UTF bytes long");
            }
        }
        return result;
    }

    public static void encode(byte[] dst, int offset, String s) {
        int length = s.length();
        for (int i = 0; i < length; i++) {
            char ch = s.charAt(i);
            if (ch != 0 && ch <= 127) {
                dst[offset] = (byte) ch;
                offset++;
            } else if (ch <= 2047) {
                int offset2 = offset + 1;
                dst[offset] = (byte) (((ch >> 6) & 31) | 192);
                offset = offset2 + 1;
                dst[offset2] = (byte) ((ch & '?') | 128);
            } else {
                int offset3 = offset + 1;
                dst[offset] = (byte) (((ch >> '\f') & 15) | 224);
                int offset4 = offset3 + 1;
                dst[offset3] = (byte) (((ch >> 6) & 63) | 128);
                dst[offset4] = (byte) ((ch & '?') | 128);
                offset = offset4 + 1;
            }
        }
    }

    private ModifiedUtf8() {
    }
}
