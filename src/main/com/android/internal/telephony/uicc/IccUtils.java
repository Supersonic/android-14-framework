package com.android.internal.telephony.uicc;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.AudioSystem;
import android.text.TextUtils;
import android.text.format.DateFormat;
import com.android.internal.C4057R;
import com.android.internal.midi.MidiConstants;
import com.android.internal.telephony.GsmAlphabet;
import com.android.telephony.Rlog;
import com.google.android.mms.pdu.CharacterSets;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;
/* loaded from: classes3.dex */
public class IccUtils {
    static final int FPLMN_BYTE_SIZE = 3;
    private static final char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', DateFormat.CAPITAL_AM_PM, 'B', 'C', 'D', DateFormat.DAY, 'F'};
    static final String LOG_TAG = "IccUtils";
    public static final String TEST_ICCID = "FFFFFFFFFFFFFFFFFFFF";

    public static String bcdToString(byte[] data, int offset, int length) {
        int v;
        StringBuilder ret = new StringBuilder(length * 2);
        for (int i = offset; i < offset + length && (v = data[i] & MidiConstants.STATUS_CHANNEL_MASK) <= 9; i++) {
            ret.append((char) (v + 48));
            int v2 = (data[i] >> 4) & 15;
            if (v2 != 15) {
                if (v2 > 9) {
                    break;
                }
                ret.append((char) (v2 + 48));
            }
        }
        return ret.toString();
    }

    public static String bcdToString(byte[] data) {
        return bcdToString(data, 0, data.length);
    }

    public static byte[] bcdToBytes(String bcd) {
        byte[] output = new byte[(bcd.length() + 1) / 2];
        bcdToBytes(bcd, output);
        return output;
    }

    public static void bcdToBytes(String bcd, byte[] bytes) {
        bcdToBytes(bcd, bytes, 0);
    }

    public static void bcdToBytes(String bcd, byte[] bytes, int offset) {
        if (bcd.length() % 2 != 0) {
            bcd = bcd + AudioSystem.LEGACY_REMOTE_SUBMIX_ADDRESS;
        }
        int size = Math.min((bytes.length - offset) * 2, bcd.length());
        int i = 0;
        int j = offset;
        while (i + 1 < size) {
            bytes[j] = (byte) ((charToByte(bcd.charAt(i + 1)) << 4) | charToByte(bcd.charAt(i)));
            i += 2;
            j++;
        }
    }

    public static String bcdPlmnToString(byte[] data, int offset) {
        if (offset + 3 > data.length) {
            return null;
        }
        byte[] trans = {(byte) ((data[offset + 0] << 4) | ((data[offset + 0] >> 4) & 15)), (byte) ((data[offset + 1] << 4) | (data[offset + 2] & MidiConstants.STATUS_CHANNEL_MASK)), (byte) ((data[offset + 2] & 240) | ((data[offset + 1] >> 4) & 15))};
        String ret = bytesToHexString(trans);
        if (ret.contains("F")) {
            return ret.replaceAll("F", "");
        }
        return ret;
    }

    public static void stringToBcdPlmn(String plmn, byte[] data, int offset) {
        char digit6 = plmn.length() > 5 ? plmn.charAt(5) : 'F';
        data[offset] = (byte) ((charToByte(plmn.charAt(1)) << 4) | charToByte(plmn.charAt(0)));
        data[offset + 1] = (byte) ((charToByte(digit6) << 4) | charToByte(plmn.charAt(2)));
        data[offset + 2] = (byte) ((charToByte(plmn.charAt(4)) << 4) | charToByte(plmn.charAt(3)));
    }

    public static String bchToString(byte[] data, int offset, int length) {
        StringBuilder ret = new StringBuilder(length * 2);
        for (int i = offset; i < offset + length; i++) {
            int v = data[i] & MidiConstants.STATUS_CHANNEL_MASK;
            char[] cArr = HEX_CHARS;
            ret.append(cArr[v]);
            int v2 = (data[i] >> 4) & 15;
            ret.append(cArr[v2]);
        }
        return ret.toString();
    }

    public static String cdmaBcdToString(byte[] data, int offset, int length) {
        StringBuilder ret = new StringBuilder(length);
        int count = 0;
        int i = offset;
        while (count < length) {
            int v = data[i] & MidiConstants.STATUS_CHANNEL_MASK;
            if (v > 9) {
                v = 0;
            }
            ret.append((char) (v + 48));
            int count2 = count + 1;
            if (count2 == length) {
                break;
            }
            int v2 = (data[i] >> 4) & 15;
            if (v2 > 9) {
                v2 = 0;
            }
            ret.append((char) (v2 + 48));
            count = count2 + 1;
            i++;
        }
        return ret.toString();
    }

    public static int gsmBcdByteToInt(byte b) {
        int ret = 0;
        if ((b & 240) <= 144) {
            ret = (b >> 4) & 15;
        }
        if ((b & MidiConstants.STATUS_CHANNEL_MASK) <= 9) {
            return ret + ((b & MidiConstants.STATUS_CHANNEL_MASK) * 10);
        }
        return ret;
    }

    public static int cdmaBcdByteToInt(byte b) {
        int ret = 0;
        if ((b & 240) <= 144) {
            ret = ((b >> 4) & 15) * 10;
        }
        if ((b & MidiConstants.STATUS_CHANNEL_MASK) <= 9) {
            return ret + (b & MidiConstants.STATUS_CHANNEL_MASK);
        }
        return ret;
    }

    public static byte[] stringToAdnStringField(String alphaTag) {
        int septets = GsmAlphabet.countGsmSeptetsUsingTables(alphaTag, false, 0, 0);
        if (septets != -1) {
            byte[] ret = new byte[septets];
            GsmAlphabet.stringToGsm8BitUnpackedField(alphaTag, ret, 0, ret.length);
            return ret;
        }
        byte[] alphaTagBytes = alphaTag.getBytes(StandardCharsets.UTF_16BE);
        byte[] ret2 = new byte[alphaTagBytes.length + 1];
        ret2[0] = Byte.MIN_VALUE;
        System.arraycopy(alphaTagBytes, 0, ret2, 1, alphaTagBytes.length);
        return ret2;
    }

    public static String adnStringFieldToString(byte[] data, int offset, int length) {
        if (length == 0) {
            return "";
        }
        if (length >= 1 && data[offset] == Byte.MIN_VALUE) {
            int ucslen = (length - 1) / 2;
            String ret = null;
            try {
                ret = new String(data, offset + 1, ucslen * 2, "utf-16be");
            } catch (UnsupportedEncodingException ex) {
                Rlog.m7e(LOG_TAG, "implausible UnsupportedEncodingException", ex);
            }
            if (ret != null) {
                int ucslen2 = ret.length();
                while (ucslen2 > 0 && ret.charAt(ucslen2 - 1) == 65535) {
                    ucslen2--;
                }
                return ret.substring(0, ucslen2);
            }
        }
        boolean isucs2 = false;
        char base = 0;
        int len = 0;
        if (length >= 3 && data[offset] == -127) {
            len = data[offset + 1] & 255;
            if (len > length - 3) {
                len = length - 3;
            }
            base = (char) ((data[offset + 2] & 255) << 7);
            offset += 3;
            isucs2 = true;
        } else if (length >= 4 && data[offset] == -126) {
            len = data[offset + 1] & 255;
            if (len > length - 4) {
                len = length - 4;
            }
            base = (char) (((data[offset + 2] & 255) << 8) | (data[offset + 3] & 255));
            offset += 4;
            isucs2 = true;
        }
        if (isucs2) {
            StringBuilder ret2 = new StringBuilder();
            while (len > 0) {
                if (data[offset] < 0) {
                    ret2.append((char) ((data[offset] & Byte.MAX_VALUE) + base));
                    offset++;
                    len--;
                }
                int count = 0;
                while (count < len && data[offset + count] >= 0) {
                    count++;
                }
                ret2.append(GsmAlphabet.gsm8BitUnpackedToString(data, offset, count));
                offset += count;
                len -= count;
            }
            return ret2.toString();
        }
        Resources resource = Resources.getSystem();
        String defaultCharset = "";
        try {
            defaultCharset = resource.getString(C4057R.string.gsm_alphabet_default_charset);
        } catch (Resources.NotFoundException e) {
        }
        return GsmAlphabet.gsm8BitUnpackedToString(data, offset, length, defaultCharset.trim());
    }

    public static int hexCharToInt(char c) {
        if (c < '0' || c > '9') {
            if (c < 'A' || c > 'F') {
                if (c < 'a' || c > 'f') {
                    throw new RuntimeException("invalid hex char '" + c + "'");
                }
                return (c - 'a') + 10;
            }
            return (c - 'A') + 10;
        }
        return c - '0';
    }

    public static byte[] hexStringToBytes(String s) {
        if (s == null) {
            return null;
        }
        int sz = s.length();
        byte[] ret = new byte[sz / 2];
        for (int i = 0; i < sz; i += 2) {
            ret[i / 2] = (byte) ((hexCharToInt(s.charAt(i)) << 4) | hexCharToInt(s.charAt(i + 1)));
        }
        return ret;
    }

    public static String bytesToHexString(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        StringBuilder ret = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            int b = (bytes[i] >> 4) & 15;
            char[] cArr = HEX_CHARS;
            ret.append(cArr[b]);
            int b2 = bytes[i] & MidiConstants.STATUS_CHANNEL_MASK;
            ret.append(cArr[b2]);
        }
        return ret.toString();
    }

    public static String networkNameToString(byte[] data, int offset, int length) {
        String ret;
        if ((data[offset] & 128) != 128 || length < 1) {
            return "";
        }
        switch ((data[offset] >>> 4) & 7) {
            case 0:
                int unusedBits = data[offset] & 7;
                int countSeptets = (((length - 1) * 8) - unusedBits) / 7;
                String ret2 = GsmAlphabet.gsm7BitPackedToString(data, offset + 1, countSeptets);
                ret = ret2;
                break;
            case 1:
                try {
                    ret = new String(data, offset + 1, length - 1, CharacterSets.MIMENAME_UTF_16);
                    break;
                } catch (UnsupportedEncodingException ex) {
                    Rlog.m7e(LOG_TAG, "implausible UnsupportedEncodingException", ex);
                    ret = "";
                    break;
                }
            default:
                ret = "";
                break;
        }
        int countSeptets2 = data[offset];
        return ret;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static Bitmap parseToBnW(byte[] data, int length) {
        int valueIndex = 0 + 1;
        int width = data[0] & 255;
        int valueIndex2 = valueIndex + 1;
        int height = data[valueIndex] & 255;
        int numOfPixels = width * height;
        int[] pixels = new int[numOfPixels];
        int pixelIndex = 0;
        byte bitIndex = 7;
        byte currentByte = 0;
        while (pixelIndex < numOfPixels) {
            if (pixelIndex % 8 == 0) {
                int valueIndex3 = valueIndex2 + 1;
                byte currentByte2 = data[valueIndex2];
                bitIndex = 7;
                currentByte = currentByte2;
                valueIndex2 = valueIndex3;
            }
            pixels[pixelIndex] = bitToRGB((currentByte >> bitIndex) & 1);
            pixelIndex++;
            bitIndex--;
        }
        if (pixelIndex != numOfPixels) {
            Rlog.m8e(LOG_TAG, "parse end and size error");
        }
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
    }

    private static int bitToRGB(int bit) {
        if (bit == 1) {
            return -1;
        }
        return -16777216;
    }

    public static Bitmap parseToRGB(byte[] data, int length, boolean transparency) {
        int[] resultArray;
        int valueIndex = 0 + 1;
        int width = data[0] & 255;
        int valueIndex2 = valueIndex + 1;
        int height = data[valueIndex] & 255;
        int valueIndex3 = valueIndex2 + 1;
        int bits = data[valueIndex2] & 255;
        int valueIndex4 = valueIndex3 + 1;
        int colorNumber = data[valueIndex3] & 255;
        int valueIndex5 = valueIndex4 + 1;
        int valueIndex6 = valueIndex5 + 1;
        int clutOffset = ((data[valueIndex4] & 255) << 8) | (data[valueIndex5] & 255);
        int[] colorIndexArray = getCLUT(data, clutOffset, colorNumber);
        if (true == transparency) {
            colorIndexArray[colorNumber - 1] = 0;
        }
        if (8 % bits == 0) {
            resultArray = mapTo2OrderBitColor(data, valueIndex6, width * height, colorIndexArray, bits);
        } else {
            resultArray = mapToNon2OrderBitColor(data, valueIndex6, width * height, colorIndexArray, bits);
        }
        return Bitmap.createBitmap(resultArray, width, height, Bitmap.Config.RGB_565);
    }

    private static int[] mapTo2OrderBitColor(byte[] data, int valueIndex, int length, int[] colorArray, int bits) {
        if (8 % bits != 0) {
            Rlog.m8e(LOG_TAG, "not event number of color");
            return mapToNon2OrderBitColor(data, valueIndex, length, colorArray, bits);
        }
        int mask = 1;
        switch (bits) {
            case 1:
                mask = 1;
                break;
            case 2:
                mask = 3;
                break;
            case 4:
                mask = 15;
                break;
            case 8:
                mask = 255;
                break;
        }
        int[] resultArray = new int[length];
        int resultIndex = 0;
        int run = 8 / bits;
        while (resultIndex < length) {
            int valueIndex2 = valueIndex + 1;
            byte tempByte = data[valueIndex];
            int runIndex = 0;
            while (runIndex < run) {
                int offset = (run - runIndex) - 1;
                resultArray[resultIndex] = colorArray[(tempByte >> (offset * bits)) & mask];
                runIndex++;
                resultIndex++;
            }
            valueIndex = valueIndex2;
        }
        return resultArray;
    }

    private static int[] mapToNon2OrderBitColor(byte[] data, int valueIndex, int length, int[] colorArray, int bits) {
        if (8 % bits == 0) {
            Rlog.m8e(LOG_TAG, "not odd number of color");
            return mapTo2OrderBitColor(data, valueIndex, length, colorArray, bits);
        }
        int[] resultArray = new int[length];
        return resultArray;
    }

    private static int[] getCLUT(byte[] rawData, int offset, int number) {
        if (rawData == null) {
            return null;
        }
        int[] result = new int[number];
        int endIndex = (number * 3) + offset;
        int valueIndex = offset;
        int colorIndex = 0;
        while (true) {
            int colorIndex2 = colorIndex + 1;
            int valueIndex2 = valueIndex + 1;
            int valueIndex3 = valueIndex2 + 1;
            int i = ((rawData[valueIndex] & 255) << 16) | (-16777216) | ((rawData[valueIndex2] & 255) << 8);
            int valueIndex4 = valueIndex3 + 1;
            result[colorIndex] = i | (rawData[valueIndex3] & 255);
            if (valueIndex4 < endIndex) {
                colorIndex = colorIndex2;
                valueIndex = valueIndex4;
            } else {
                return result;
            }
        }
    }

    public static String getDecimalSubstring(String iccId) {
        int position = 0;
        while (position < iccId.length() && Character.isDigit(iccId.charAt(position))) {
            position++;
        }
        return iccId.substring(0, position);
    }

    public static int bytesToInt(byte[] src, int offset, int length) {
        if (length > 4) {
            throw new IllegalArgumentException("length must be <= 4 (only 32-bit integer supported): " + length);
        }
        if (offset < 0 || length < 0 || offset + length > src.length) {
            throw new IndexOutOfBoundsException("Out of the bounds: src=[" + src.length + "], offset=" + offset + ", length=" + length);
        }
        int result = 0;
        for (int i = 0; i < length; i++) {
            result = (result << 8) | (src[offset + i] & 255);
        }
        if (result < 0) {
            throw new IllegalArgumentException("src cannot be parsed as a positive integer: " + result);
        }
        return result;
    }

    public static long bytesToRawLong(byte[] src, int offset, int length) {
        if (length > 8) {
            throw new IllegalArgumentException("length must be <= 8 (only 64-bit long supported): " + length);
        }
        if (offset < 0 || length < 0 || offset + length > src.length) {
            throw new IndexOutOfBoundsException("Out of the bounds: src=[" + src.length + "], offset=" + offset + ", length=" + length);
        }
        long result = 0;
        for (int i = 0; i < length; i++) {
            result = (result << 8) | (src[offset + i] & 255);
        }
        return result;
    }

    public static byte[] unsignedIntToBytes(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("value must be 0 or positive: " + value);
        }
        byte[] bytes = new byte[byteNumForUnsignedInt(value)];
        unsignedIntToBytes(value, bytes, 0);
        return bytes;
    }

    public static byte[] signedIntToBytes(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("value must be 0 or positive: " + value);
        }
        byte[] bytes = new byte[byteNumForSignedInt(value)];
        signedIntToBytes(value, bytes, 0);
        return bytes;
    }

    public static int unsignedIntToBytes(int value, byte[] dest, int offset) {
        return intToBytes(value, dest, offset, false);
    }

    public static int signedIntToBytes(int value, byte[] dest, int offset) {
        return intToBytes(value, dest, offset, true);
    }

    public static int byteNumForUnsignedInt(int value) {
        return byteNumForInt(value, false);
    }

    public static int byteNumForSignedInt(int value) {
        return byteNumForInt(value, true);
    }

    private static int intToBytes(int value, byte[] dest, int offset, boolean signed) {
        int l = byteNumForInt(value, signed);
        if (offset < 0 || offset + l > dest.length) {
            throw new IndexOutOfBoundsException("Not enough space to write. Required bytes: " + l);
        }
        int i = l - 1;
        int v = value;
        while (i >= 0) {
            byte b = (byte) (v & 255);
            dest[offset + i] = b;
            i--;
            v >>>= 8;
        }
        return l;
    }

    private static int byteNumForInt(int value, boolean signed) {
        if (value >= 0) {
            if (signed) {
                if (value <= 127) {
                    return 1;
                }
                if (value <= 32767) {
                    return 2;
                }
                if (value <= 8388607) {
                    return 3;
                }
                return 4;
            } else if (value <= 255) {
                return 1;
            } else {
                if (value <= 65535) {
                    return 2;
                }
                if (value <= 16777215) {
                    return 3;
                }
                return 4;
            }
        }
        throw new IllegalArgumentException("value must be 0 or positive: " + value);
    }

    public static byte countTrailingZeros(byte b) {
        if (b == 0) {
            return (byte) 8;
        }
        int v = b & 255;
        byte c = 7;
        if ((v & 15) != 0) {
            c = (byte) (7 - 4);
        }
        if ((v & 51) != 0) {
            c = (byte) (c - 2);
        }
        if ((v & 85) != 0) {
            return (byte) (c - 1);
        }
        return c;
    }

    public static String byteToHex(byte b) {
        char[] cArr = HEX_CHARS;
        return new String(new char[]{cArr[(b & 255) >>> 4], cArr[b & MidiConstants.STATUS_CHANNEL_MASK]});
    }

    public static String stripTrailingFs(String s) {
        if ("FFFFFFFFFFFFFFFFFFFF".equals(s)) {
            return s;
        }
        if (s == null) {
            return null;
        }
        return s.replaceAll("(?i)f*$", "");
    }

    public static boolean compareIgnoreTrailingFs(String a, String b) {
        return TextUtils.equals(a, b) || TextUtils.equals(stripTrailingFs(a), stripTrailingFs(b));
    }

    private static byte charToByte(char c) {
        if (c >= '0' && c <= '9') {
            return (byte) (c - '0');
        }
        if (c >= 'A' && c <= 'F') {
            return (byte) (c - '7');
        }
        if (c >= 'a' && c <= 'f') {
            return (byte) (c - 'W');
        }
        return (byte) 0;
    }

    public static byte[] encodeFplmns(List<String> fplmns, int dataLength) {
        byte[] serializedFplmns = new byte[dataLength];
        int offset = 0;
        for (String fplmn : fplmns) {
            if (offset >= dataLength) {
                break;
            }
            stringToBcdPlmn(fplmn, serializedFplmns, offset);
            offset += 3;
        }
        while (offset < dataLength) {
            serializedFplmns[offset] = -1;
            offset++;
        }
        return serializedFplmns;
    }
}
