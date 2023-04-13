package android.content.integrity;

import com.android.internal.midi.MidiConstants;
import com.android.internal.util.Preconditions;
/* loaded from: classes.dex */
public class IntegrityUtils {
    private static final char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();

    public static byte[] getBytesFromHexDigest(String hexDigest) {
        Preconditions.checkArgument(hexDigest.length() % 2 == 0, "Invalid hex encoding %s: must have even length", hexDigest);
        byte[] rawBytes = new byte[hexDigest.length() / 2];
        for (int i = 0; i < rawBytes.length; i++) {
            int upperNibble = hexDigest.charAt(i * 2);
            int lowerNibble = hexDigest.charAt((i * 2) + 1);
            rawBytes[i] = (byte) ((hexToDec(upperNibble) << 4) | hexToDec(lowerNibble));
        }
        return rawBytes;
    }

    public static String getHexDigest(byte[] rawBytes) {
        char[] hexChars = new char[rawBytes.length * 2];
        for (int i = 0; i < rawBytes.length; i++) {
            int upperNibble = (rawBytes[i] >>> 4) & 15;
            int lowerNibble = rawBytes[i] & MidiConstants.STATUS_CHANNEL_MASK;
            hexChars[i * 2] = decToHex(upperNibble);
            hexChars[(i * 2) + 1] = decToHex(lowerNibble);
        }
        return new String(hexChars);
    }

    private static int hexToDec(int hexChar) {
        if (hexChar >= 48 && hexChar <= 57) {
            return hexChar - 48;
        }
        if (hexChar >= 97 && hexChar <= 102) {
            return (hexChar - 97) + 10;
        }
        if (hexChar >= 65 && hexChar <= 70) {
            return (hexChar - 65) + 10;
        }
        throw new IllegalArgumentException("Invalid hex char " + hexChar);
    }

    private static char decToHex(int dec) {
        if (dec >= 0) {
            char[] cArr = HEX_CHARS;
            if (dec < cArr.length) {
                return cArr[dec];
            }
        }
        throw new IllegalArgumentException("Invalid dec value to be converted to hex digit " + dec);
    }
}
