package com.android.internal.org.bouncycastle.util.encoders;

import com.android.internal.midi.MidiConstants;
/* loaded from: classes4.dex */
public class UTF8 {
    private static final byte C_CR1 = 1;
    private static final byte C_CR2 = 2;
    private static final byte C_CR3 = 3;
    private static final byte C_ILL = 0;
    private static final byte C_L2A = 4;
    private static final byte C_L3A = 5;
    private static final byte C_L3B = 6;
    private static final byte C_L3C = 7;
    private static final byte C_L4A = 8;
    private static final byte C_L4B = 9;
    private static final byte C_L4C = 10;
    private static final byte S_CS1 = 0;
    private static final byte S_CS2 = 16;
    private static final byte S_CS3 = 32;
    private static final byte S_END = -1;
    private static final byte S_ERR = -2;
    private static final byte S_P3A = 48;
    private static final byte S_P3B = 64;
    private static final byte S_P4A = 80;
    private static final byte S_P4B = 96;
    private static final short[] firstUnitTable = new short[128];
    private static final byte[] transitionTable;

    static {
        byte[] bArr = new byte[112];
        transitionTable = bArr;
        byte[] categories = new byte[128];
        fill(categories, 0, 15, (byte) 1);
        fill(categories, 16, 31, (byte) 2);
        fill(categories, 32, 63, (byte) 3);
        fill(categories, 64, 65, (byte) 0);
        fill(categories, 66, 95, (byte) 4);
        fill(categories, 96, 96, (byte) 5);
        fill(categories, 97, 108, (byte) 6);
        fill(categories, 109, 109, (byte) 7);
        fill(categories, 110, 111, (byte) 6);
        fill(categories, 112, 112, (byte) 8);
        fill(categories, 113, 115, (byte) 9);
        fill(categories, 116, 116, (byte) 10);
        fill(categories, 117, 127, (byte) 0);
        fill(bArr, 0, bArr.length - 1, (byte) -2);
        fill(bArr, 8, 11, (byte) -1);
        fill(bArr, 24, 27, (byte) 0);
        fill(bArr, 40, 43, (byte) 16);
        fill(bArr, 58, 59, (byte) 0);
        fill(bArr, 72, 73, (byte) 0);
        fill(bArr, 89, 91, (byte) 16);
        fill(bArr, 104, 104, (byte) 16);
        byte[] firstUnitMasks = {0, 0, 0, 0, 31, MidiConstants.STATUS_CHANNEL_MASK, MidiConstants.STATUS_CHANNEL_MASK, MidiConstants.STATUS_CHANNEL_MASK, 7, 7, 7};
        byte[] firstUnitTransitions = {-2, -2, -2, -2, 0, S_P3A, 16, 64, S_P4A, 32, S_P4B};
        for (int i = 0; i < 128; i++) {
            byte category = categories[i];
            int codePoint = firstUnitMasks[category] & i;
            byte state = firstUnitTransitions[category];
            firstUnitTable[i] = (short) ((codePoint << 8) | state);
        }
    }

    private static void fill(byte[] table, int first, int last, byte b) {
        for (int i = first; i <= last; i++) {
            table[i] = b;
        }
    }

    public static int transcodeToUTF16(byte[] utf8, char[] utf16) {
        int i = 0;
        int j = 0;
        while (i < utf8.length) {
            int i2 = i + 1;
            byte codeUnit = utf8[i];
            if (codeUnit >= 0) {
                if (j >= utf16.length) {
                    return -1;
                }
                utf16[j] = (char) codeUnit;
                i = i2;
                j++;
            } else {
                short first = firstUnitTable[codeUnit & Byte.MAX_VALUE];
                int codePoint = first >>> 8;
                byte state = (byte) first;
                i = i2;
                while (state >= 0) {
                    if (i >= utf8.length) {
                        return -1;
                    }
                    byte codeUnit2 = utf8[i];
                    codePoint = (codePoint << 6) | (codeUnit2 & 63);
                    state = transitionTable[((codeUnit2 & 255) >>> 4) + state];
                    i++;
                }
                if (state == -2) {
                    return -1;
                }
                if (codePoint <= 65535) {
                    if (j >= utf16.length) {
                        return -1;
                    }
                    utf16[j] = (char) codePoint;
                    j++;
                } else if (j >= utf16.length - 1) {
                    return -1;
                } else {
                    int j2 = j + 1;
                    utf16[j] = (char) ((codePoint >>> 10) + 55232);
                    j = j2 + 1;
                    utf16[j2] = (char) ((codePoint & 1023) | 56320);
                }
            }
        }
        return j;
    }
}
