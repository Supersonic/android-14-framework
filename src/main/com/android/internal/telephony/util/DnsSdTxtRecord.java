package com.android.internal.telephony.util;

import android.os.Parcel;
import android.os.Parcelable;
import com.android.internal.telephony.PhoneConfigurationManager;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
/* loaded from: classes.dex */
public class DnsSdTxtRecord implements Parcelable {
    public static final Parcelable.Creator<DnsSdTxtRecord> CREATOR = new Parcelable.Creator<DnsSdTxtRecord>() { // from class: com.android.internal.telephony.util.DnsSdTxtRecord.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DnsSdTxtRecord createFromParcel(Parcel parcel) {
            DnsSdTxtRecord dnsSdTxtRecord = new DnsSdTxtRecord();
            parcel.readByteArray(dnsSdTxtRecord.mData);
            return dnsSdTxtRecord;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DnsSdTxtRecord[] newArray(int i) {
            return new DnsSdTxtRecord[i];
        }
    };
    private byte[] mData;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public DnsSdTxtRecord() {
        this.mData = new byte[0];
    }

    public DnsSdTxtRecord(byte[] bArr) {
        this.mData = (byte[]) bArr.clone();
    }

    public DnsSdTxtRecord(DnsSdTxtRecord dnsSdTxtRecord) {
        byte[] bArr;
        if (dnsSdTxtRecord == null || (bArr = dnsSdTxtRecord.mData) == null) {
            return;
        }
        this.mData = (byte[]) bArr.clone();
    }

    public void set(String str, String str2) {
        byte[] bArr;
        int i;
        if (str2 != null) {
            bArr = str2.getBytes();
            i = bArr.length;
        } else {
            bArr = null;
            i = 0;
        }
        try {
            byte[] bytes = str.getBytes("US-ASCII");
            for (byte b : bytes) {
                if (b == 61) {
                    throw new IllegalArgumentException("= is not a valid character in key");
                }
            }
            if (bytes.length + i >= 255) {
                throw new IllegalArgumentException("Key and Value length cannot exceed 255 bytes");
            }
            int remove = remove(str);
            if (remove == -1) {
                remove = keyCount();
            }
            insert(bytes, bArr, remove);
        } catch (UnsupportedEncodingException unused) {
            throw new IllegalArgumentException("key should be US-ASCII");
        }
    }

    public String get(String str) {
        byte[] value = getValue(str);
        if (value != null) {
            return new String(value);
        }
        return null;
    }

    public int remove(String str) {
        int i = 0;
        int i2 = 0;
        while (true) {
            byte[] bArr = this.mData;
            if (i >= bArr.length) {
                return -1;
            }
            int i3 = bArr[i];
            if (str.length() <= i3 && ((str.length() == i3 || this.mData[str.length() + i + 1] == 61) && str.compareToIgnoreCase(new String(this.mData, i + 1, str.length())) == 0)) {
                byte[] bArr2 = this.mData;
                byte[] bArr3 = new byte[(bArr2.length - i3) - 1];
                this.mData = bArr3;
                System.arraycopy(bArr2, 0, bArr3, 0, i);
                System.arraycopy(bArr2, i + i3 + 1, this.mData, i, ((bArr2.length - i) - i3) - 1);
                return i2;
            }
            i += (i3 + 1) & 255;
            i2++;
        }
    }

    public int keyCount() {
        int i = 0;
        int i2 = 0;
        while (true) {
            byte[] bArr = this.mData;
            if (i >= bArr.length) {
                return i2;
            }
            i += (bArr[i] + 1) & 255;
            i2++;
        }
    }

    public boolean contains(String str) {
        int i = 0;
        while (true) {
            String key = getKey(i);
            if (key == null) {
                return false;
            }
            if (str.compareToIgnoreCase(key) == 0) {
                return true;
            }
            i++;
        }
    }

    public int size() {
        return this.mData.length;
    }

    public byte[] getRawData() {
        return (byte[]) this.mData.clone();
    }

    private void insert(byte[] bArr, byte[] bArr2, int i) {
        byte[] bArr3 = this.mData;
        int length = bArr2 != null ? bArr2.length : 0;
        int i2 = 0;
        for (int i3 = 0; i3 < i; i3++) {
            byte[] bArr4 = this.mData;
            if (i2 >= bArr4.length) {
                break;
            }
            i2 += (bArr4[i2] + 1) & 255;
        }
        int length2 = bArr.length + length + (bArr2 != null ? 1 : 0);
        int length3 = bArr3.length + length2 + 1;
        byte[] bArr5 = new byte[length3];
        this.mData = bArr5;
        System.arraycopy(bArr3, 0, bArr5, 0, i2);
        int length4 = bArr3.length - i2;
        System.arraycopy(bArr3, i2, this.mData, length3 - length4, length4);
        byte[] bArr6 = this.mData;
        bArr6[i2] = (byte) length2;
        int i4 = i2 + 1;
        System.arraycopy(bArr, 0, bArr6, i4, bArr.length);
        if (bArr2 != null) {
            byte[] bArr7 = this.mData;
            bArr7[i4 + bArr.length] = 61;
            System.arraycopy(bArr2, 0, bArr7, i2 + bArr.length + 2, length);
        }
    }

    private String getKey(int i) {
        int i2 = 0;
        int i3 = 0;
        for (int i4 = 0; i4 < i; i4++) {
            byte[] bArr = this.mData;
            if (i3 >= bArr.length) {
                break;
            }
            i3 += bArr[i3] + 1;
        }
        byte[] bArr2 = this.mData;
        if (i3 < bArr2.length) {
            byte b = bArr2[i3];
            while (i2 < b && this.mData[i3 + i2 + 1] != 61) {
                i2++;
            }
            return new String(this.mData, i3 + 1, i2);
        }
        return null;
    }

    private byte[] getValue(int i) {
        int i2 = 0;
        for (int i3 = 0; i3 < i; i3++) {
            byte[] bArr = this.mData;
            if (i2 >= bArr.length) {
                break;
            }
            i2 += bArr[i2] + 1;
        }
        byte[] bArr2 = this.mData;
        if (i2 < bArr2.length) {
            int i4 = bArr2[i2];
            for (int i5 = 0; i5 < i4; i5++) {
                byte[] bArr3 = this.mData;
                int i6 = i2 + i5;
                if (bArr3[i6 + 1] == 61) {
                    int i7 = (i4 - i5) - 1;
                    byte[] bArr4 = new byte[i7];
                    System.arraycopy(bArr3, i6 + 2, bArr4, 0, i7);
                    return bArr4;
                }
            }
        }
        return null;
    }

    private String getValueAsString(int i) {
        byte[] value = getValue(i);
        if (value != null) {
            return new String(value);
        }
        return null;
    }

    private byte[] getValue(String str) {
        int i = 0;
        while (true) {
            String key = getKey(i);
            if (key == null) {
                return null;
            }
            if (str.compareToIgnoreCase(key) == 0) {
                return getValue(i);
            }
            i++;
        }
    }

    public String toString() {
        String str;
        String str2 = null;
        int i = 0;
        while (true) {
            String key = getKey(i);
            if (key == null) {
                break;
            }
            String str3 = "{" + key;
            String valueAsString = getValueAsString(i);
            if (valueAsString != null) {
                str = str3 + "=" + valueAsString + "}";
            } else {
                str = str3 + "}";
            }
            if (str2 == null) {
                str2 = str;
            } else {
                str2 = str2 + ", " + str;
            }
            i++;
        }
        return str2 != null ? str2 : PhoneConfigurationManager.SSSS;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof DnsSdTxtRecord) {
            return Arrays.equals(((DnsSdTxtRecord) obj).mData, this.mData);
        }
        return false;
    }

    public int hashCode() {
        return Arrays.hashCode(this.mData);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByteArray(this.mData);
    }
}
