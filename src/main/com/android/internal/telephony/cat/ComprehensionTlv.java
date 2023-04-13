package com.android.internal.telephony.cat;

import android.compat.annotation.UnsupportedAppUsage;
import com.android.telephony.Rlog;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class ComprehensionTlv {
    private boolean mCr;
    private int mLength;
    private byte[] mRawValue;
    private int mTag;
    private int mValueIndex;

    public ComprehensionTlv(int i, boolean z, int i2, byte[] bArr, int i3) {
        this.mTag = i;
        this.mCr = z;
        this.mLength = i2;
        this.mValueIndex = i3;
        this.mRawValue = bArr;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public int getTag() {
        return this.mTag;
    }

    public boolean isComprehensionRequired() {
        return this.mCr;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public int getLength() {
        return this.mLength;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public int getValueIndex() {
        return this.mValueIndex;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public byte[] getRawValue() {
        return this.mRawValue;
    }

    public static List<ComprehensionTlv> decodeMany(byte[] bArr, int i) throws ResultException {
        ArrayList arrayList = new ArrayList();
        int length = bArr.length;
        while (true) {
            if (i < length) {
                ComprehensionTlv decode = decode(bArr, i);
                if (decode != null) {
                    arrayList.add(decode);
                    i = decode.mLength + decode.mValueIndex;
                } else {
                    CatLog.m4d("ComprehensionTlv", "decodeMany: ctlv is null, stop decoding");
                    break;
                }
            } else {
                break;
            }
        }
        return arrayList;
    }

    public static ComprehensionTlv decode(byte[] bArr, int i) throws ResultException {
        boolean z;
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        int length = bArr.length;
        int i7 = i + 1;
        try {
            int i8 = bArr[i] & 255;
            if (i8 != 0 && i8 != 255) {
                if (i8 == 127) {
                    int i9 = ((bArr[i7] & 255) << 8) | (bArr[i7 + 1] & 255);
                    z = (32768 & i9) != 0;
                    i2 = i9 & (-32769);
                    i7 += 2;
                } else if (i8 != 128) {
                    z = (i8 & 128) != 0;
                    i2 = i8 & (-129);
                }
                int i10 = i7 + 1;
                try {
                    int i11 = bArr[i7] & 255;
                    if (i11 < 128) {
                        i6 = i10;
                        i5 = i11;
                    } else if (i11 == 129) {
                        int i12 = i10 + 1;
                        int i13 = 255 & bArr[i10];
                        if (i13 < 128) {
                            throw new ResultException(ResultCode.CMD_DATA_NOT_UNDERSTOOD, "length < 0x80 length=" + Integer.toHexString(i13) + " startIndex=" + i + " curIndex=" + i12 + " endIndex=" + length);
                        }
                        i6 = i12;
                        i5 = i13;
                    } else {
                        try {
                            if (i11 == 130) {
                                i3 = ((bArr[i10] & 255) << 8) | (255 & bArr[i10 + 1]);
                                i4 = i10 + 2;
                                if (i3 < 256) {
                                    throw new ResultException(ResultCode.CMD_DATA_NOT_UNDERSTOOD, "two byte length < 0x100 length=" + Integer.toHexString(i3) + " startIndex=" + i + " curIndex=" + i4 + " endIndex=" + length);
                                }
                            } else if (i11 == 131) {
                                i3 = ((bArr[i10] & 255) << 16) | ((bArr[i10 + 1] & 255) << 8) | (255 & bArr[i10 + 2]);
                                i4 = i10 + 3;
                                if (i3 < 65536) {
                                    throw new ResultException(ResultCode.CMD_DATA_NOT_UNDERSTOOD, "three byte length < 0x10000 length=0x" + Integer.toHexString(i3) + " startIndex=" + i + " curIndex=" + i4 + " endIndex=" + length);
                                }
                            } else {
                                throw new ResultException(ResultCode.CMD_DATA_NOT_UNDERSTOOD, "Bad length modifer=" + i11 + " startIndex=" + i + " curIndex=" + i10 + " endIndex=" + length);
                            }
                            i5 = i3;
                            i6 = i4;
                        } catch (IndexOutOfBoundsException unused) {
                            i7 = 255;
                            throw new ResultException(ResultCode.CMD_DATA_NOT_UNDERSTOOD, "IndexOutOfBoundsException startIndex=" + i + " curIndex=" + i7 + " endIndex=" + length);
                        }
                    }
                } catch (IndexOutOfBoundsException unused2) {
                    i7 = i10;
                }
                try {
                    return new ComprehensionTlv(i2, z, i5, bArr, i6);
                } catch (IndexOutOfBoundsException unused3) {
                    i7 = i6;
                    throw new ResultException(ResultCode.CMD_DATA_NOT_UNDERSTOOD, "IndexOutOfBoundsException startIndex=" + i + " curIndex=" + i7 + " endIndex=" + length);
                }
            }
            Rlog.d("CAT     ", "decode: unexpected first tag byte=" + Integer.toHexString(i8) + ", startIndex=" + i + " curIndex=" + i7 + " endIndex=" + length);
            return null;
        } catch (IndexOutOfBoundsException unused4) {
        }
    }
}
