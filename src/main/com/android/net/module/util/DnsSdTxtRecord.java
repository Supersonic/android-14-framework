package com.android.net.module.util;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
/* loaded from: classes5.dex */
public class DnsSdTxtRecord implements Parcelable {
    public static final Parcelable.Creator<DnsSdTxtRecord> CREATOR = new Parcelable.Creator<DnsSdTxtRecord>() { // from class: com.android.net.module.util.DnsSdTxtRecord.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DnsSdTxtRecord createFromParcel(Parcel in) {
            DnsSdTxtRecord info = new DnsSdTxtRecord();
            in.readByteArray(info.mData);
            return info;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DnsSdTxtRecord[] newArray(int size) {
            return new DnsSdTxtRecord[size];
        }
    };
    private static final byte mSeparator = 61;
    private byte[] mData;

    public DnsSdTxtRecord() {
        this.mData = new byte[0];
    }

    public DnsSdTxtRecord(byte[] data) {
        this.mData = (byte[]) data.clone();
    }

    public DnsSdTxtRecord(DnsSdTxtRecord src) {
        byte[] bArr;
        if (src != null && (bArr = src.mData) != null) {
            this.mData = (byte[]) bArr.clone();
        }
    }

    public void set(String key, String value) {
        byte[] valBytes;
        int valLen;
        if (value != null) {
            valBytes = value.getBytes();
            valLen = valBytes.length;
        } else {
            valBytes = null;
            valLen = 0;
        }
        try {
            byte[] keyBytes = key.getBytes("US-ASCII");
            for (byte b : keyBytes) {
                if (b == 61) {
                    throw new IllegalArgumentException("= is not a valid character in key");
                }
            }
            int i = keyBytes.length;
            if (i + valLen >= 255) {
                throw new IllegalArgumentException("Key and Value length cannot exceed 255 bytes");
            }
            int currentLoc = remove(key);
            if (currentLoc == -1) {
                currentLoc = keyCount();
            }
            insert(keyBytes, valBytes, currentLoc);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("key should be US-ASCII");
        }
    }

    public String get(String key) {
        byte[] val = getValue(key);
        if (val != null) {
            return new String(val);
        }
        return null;
    }

    public int remove(String key) {
        int avStart = 0;
        int i = 0;
        while (true) {
            byte[] bArr = this.mData;
            if (avStart < bArr.length) {
                int avLen = bArr[avStart];
                if (key.length() <= avLen && (key.length() == avLen || this.mData[key.length() + avStart + 1] == 61)) {
                    String s = new String(this.mData, avStart + 1, key.length());
                    if (key.compareToIgnoreCase(s) == 0) {
                        byte[] oldBytes = this.mData;
                        byte[] bArr2 = new byte[(oldBytes.length - avLen) - 1];
                        this.mData = bArr2;
                        System.arraycopy(oldBytes, 0, bArr2, 0, avStart);
                        System.arraycopy(oldBytes, avStart + avLen + 1, this.mData, avStart, ((oldBytes.length - avStart) - avLen) - 1);
                        return i;
                    }
                }
                avStart += (avLen + 1) & 255;
                i++;
            } else {
                return -1;
            }
        }
    }

    public int keyCount() {
        int count = 0;
        int nextKey = 0;
        while (true) {
            byte[] bArr = this.mData;
            if (nextKey < bArr.length) {
                nextKey += (bArr[nextKey] + 1) & 255;
                count++;
            } else {
                return count;
            }
        }
    }

    public boolean contains(String key) {
        int i = 0;
        while (true) {
            String s = getKey(i);
            if (s != null) {
                if (key.compareToIgnoreCase(s) == 0) {
                    return true;
                }
                i++;
            } else {
                return false;
            }
        }
    }

    public int size() {
        return this.mData.length;
    }

    public byte[] getRawData() {
        return (byte[]) this.mData.clone();
    }

    private void insert(byte[] keyBytes, byte[] value, int index) {
        byte[] oldBytes = this.mData;
        int valLen = value != null ? value.length : 0;
        int insertion = 0;
        for (int i = 0; i < index; i++) {
            byte[] bArr = this.mData;
            if (insertion >= bArr.length) {
                break;
            }
            insertion += (bArr[insertion] + 1) & 255;
        }
        int i2 = keyBytes.length;
        int avLen = i2 + valLen + (value != null ? 1 : 0);
        int newLen = oldBytes.length + avLen + 1;
        byte[] bArr2 = new byte[newLen];
        this.mData = bArr2;
        System.arraycopy(oldBytes, 0, bArr2, 0, insertion);
        int secondHalfLen = oldBytes.length - insertion;
        System.arraycopy(oldBytes, insertion, this.mData, newLen - secondHalfLen, secondHalfLen);
        byte[] bArr3 = this.mData;
        bArr3[insertion] = (byte) avLen;
        System.arraycopy(keyBytes, 0, bArr3, insertion + 1, keyBytes.length);
        if (value != null) {
            byte[] bArr4 = this.mData;
            bArr4[insertion + 1 + keyBytes.length] = mSeparator;
            System.arraycopy(value, 0, bArr4, keyBytes.length + insertion + 2, valLen);
        }
    }

    private String getKey(int index) {
        int avStart = 0;
        for (int i = 0; i < index; i++) {
            byte[] bArr = this.mData;
            if (avStart >= bArr.length) {
                break;
            }
            avStart += bArr[avStart] + 1;
        }
        byte[] bArr2 = this.mData;
        if (avStart < bArr2.length) {
            int avLen = bArr2[avStart];
            int aLen = 0;
            while (aLen < avLen && this.mData[avStart + aLen + 1] != 61) {
                aLen++;
            }
            return new String(this.mData, avStart + 1, aLen);
        }
        return null;
    }

    private byte[] getValue(int index) {
        int avStart = 0;
        for (int i = 0; i < index; i++) {
            byte[] bArr = this.mData;
            if (avStart >= bArr.length) {
                break;
            }
            avStart += bArr[avStart] + 1;
        }
        byte[] bArr2 = this.mData;
        if (avStart >= bArr2.length) {
            return null;
        }
        int avLen = bArr2[avStart];
        for (int aLen = 0; aLen < avLen; aLen++) {
            byte[] bArr3 = this.mData;
            if (bArr3[avStart + aLen + 1] == 61) {
                byte[] value = new byte[(avLen - aLen) - 1];
                System.arraycopy(bArr3, avStart + aLen + 2, value, 0, (avLen - aLen) - 1);
                return value;
            }
        }
        return null;
    }

    private String getValueAsString(int index) {
        byte[] value = getValue(index);
        if (value != null) {
            return new String(value);
        }
        return null;
    }

    private byte[] getValue(String forKey) {
        int i = 0;
        while (true) {
            String s = getKey(i);
            if (s != null) {
                if (forKey.compareToIgnoreCase(s) != 0) {
                    i++;
                } else {
                    return getValue(i);
                }
            } else {
                return null;
            }
        }
    }

    public String toString() {
        String result = null;
        int i = 0;
        while (true) {
            String a = getKey(i);
            if (a == null) {
                break;
            }
            String av = "{" + a;
            String val = getValueAsString(i);
            String av2 = val != null ? av + "=" + val + "}" : av + "}";
            if (result == null) {
                result = av2;
            } else {
                result = result + ", " + av2;
            }
            i++;
        }
        return result != null ? result : "";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof DnsSdTxtRecord)) {
            return false;
        }
        DnsSdTxtRecord record = (DnsSdTxtRecord) o;
        return Arrays.equals(record.mData, this.mData);
    }

    public int hashCode() {
        return Arrays.hashCode(this.mData);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByteArray(this.mData);
    }
}
