package com.google.android.mms.pdu;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
/* loaded from: classes5.dex */
public class EncodedStringValue implements Cloneable {
    private static final boolean DEBUG = false;
    private static final boolean LOCAL_LOGV = false;
    private static final String TAG = "EncodedStringValue";
    private int mCharacterSet;
    private byte[] mData;

    public EncodedStringValue(int charset, byte[] data) {
        if (data == null) {
            throw new NullPointerException("EncodedStringValue: Text-string is null.");
        }
        this.mCharacterSet = charset;
        byte[] bArr = new byte[data.length];
        this.mData = bArr;
        System.arraycopy(data, 0, bArr, 0, data.length);
    }

    public EncodedStringValue(byte[] data) {
        this(106, data);
    }

    public EncodedStringValue(String data) {
        try {
            this.mData = data.getBytes("utf-8");
            this.mCharacterSet = 106;
        } catch (UnsupportedEncodingException e) {
            Log.m109e(TAG, "Default encoding must be supported.", e);
        }
    }

    public int getCharacterSet() {
        return this.mCharacterSet;
    }

    public void setCharacterSet(int charset) {
        this.mCharacterSet = charset;
    }

    public byte[] getTextString() {
        byte[] bArr = this.mData;
        byte[] byteArray = new byte[bArr.length];
        System.arraycopy(bArr, 0, byteArray, 0, bArr.length);
        return byteArray;
    }

    public void setTextString(byte[] textString) {
        if (textString == null) {
            throw new NullPointerException("EncodedStringValue: Text-string is null.");
        }
        byte[] bArr = new byte[textString.length];
        this.mData = bArr;
        System.arraycopy(textString, 0, bArr, 0, textString.length);
    }

    public String getString() {
        int i = this.mCharacterSet;
        if (i == 0) {
            return new String(this.mData);
        }
        try {
            String name = CharacterSets.getMimeName(i);
            return new String(this.mData, name);
        } catch (UnsupportedEncodingException e) {
            try {
                return new String(this.mData, CharacterSets.MIMENAME_ISO_8859_1);
            } catch (UnsupportedEncodingException e2) {
                return new String(this.mData);
            }
        }
    }

    public void appendTextString(byte[] textString) {
        if (textString == null) {
            throw new NullPointerException("Text-string is null.");
        }
        if (this.mData == null) {
            byte[] bArr = new byte[textString.length];
            this.mData = bArr;
            System.arraycopy(textString, 0, bArr, 0, textString.length);
            return;
        }
        ByteArrayOutputStream newTextString = new ByteArrayOutputStream();
        try {
            newTextString.write(this.mData);
            newTextString.write(textString);
            this.mData = newTextString.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw new NullPointerException("appendTextString: failed when write a new Text-string");
        }
    }

    public Object clone() throws CloneNotSupportedException {
        byte[] bArr = this.mData;
        int len = bArr.length;
        byte[] dstBytes = new byte[len];
        System.arraycopy(bArr, 0, dstBytes, 0, len);
        try {
            return new EncodedStringValue(this.mCharacterSet, dstBytes);
        } catch (Exception e) {
            Log.m110e(TAG, "failed to clone an EncodedStringValue: " + this);
            e.printStackTrace();
            throw new CloneNotSupportedException(e.getMessage());
        }
    }

    public EncodedStringValue[] split(String pattern) {
        String[] temp = getString().split(pattern);
        EncodedStringValue[] ret = new EncodedStringValue[temp.length];
        for (int i = 0; i < ret.length; i++) {
            try {
                ret[i] = new EncodedStringValue(this.mCharacterSet, temp[i].getBytes());
            } catch (NullPointerException e) {
                return null;
            }
        }
        return ret;
    }

    public static EncodedStringValue[] extract(String src) {
        String[] values = src.split(NavigationBarInflaterView.GRAVITY_SEPARATOR);
        ArrayList<EncodedStringValue> list = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            if (values[i].length() > 0) {
                list.add(new EncodedStringValue(values[i]));
            }
        }
        int len = list.size();
        if (len > 0) {
            return (EncodedStringValue[]) list.toArray(new EncodedStringValue[len]);
        }
        return null;
    }

    public static String concat(EncodedStringValue[] addr) {
        StringBuilder sb = new StringBuilder();
        int maxIndex = addr.length - 1;
        for (int i = 0; i <= maxIndex; i++) {
            sb.append(addr[i].getString());
            if (i < maxIndex) {
                sb.append(NavigationBarInflaterView.GRAVITY_SEPARATOR);
            }
        }
        return sb.toString();
    }

    public static EncodedStringValue copy(EncodedStringValue value) {
        if (value == null) {
            return null;
        }
        return new EncodedStringValue(value.mCharacterSet, value.mData);
    }

    public static EncodedStringValue[] encodeStrings(String[] array) {
        int count = array.length;
        if (count > 0) {
            EncodedStringValue[] encodedArray = new EncodedStringValue[count];
            for (int i = 0; i < count; i++) {
                encodedArray[i] = new EncodedStringValue(array[i]);
            }
            return encodedArray;
        }
        return null;
    }
}
