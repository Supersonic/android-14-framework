package com.android.internal.telephony.cat;

import java.util.List;
/* loaded from: classes.dex */
class BerTlv {
    public static final int BER_EVENT_DOWNLOAD_TAG = 214;
    public static final int BER_MENU_SELECTION_TAG = 211;
    public static final int BER_PROACTIVE_COMMAND_TAG = 208;
    public static final int BER_UNKNOWN_TAG = 0;
    private List<ComprehensionTlv> mCompTlvs;
    private boolean mLengthValid;
    private int mTag;

    private BerTlv(int i, List<ComprehensionTlv> list, boolean z) {
        this.mTag = i;
        this.mCompTlvs = list;
        this.mLengthValid = z;
    }

    public List<ComprehensionTlv> getComprehensionTlvs() {
        return this.mCompTlvs;
    }

    public int getTag() {
        return this.mTag;
    }

    public boolean isLengthValid() {
        return this.mLengthValid;
    }

    /* JADX WARN: Code restructure failed: missing block: B:44:0x00bf, code lost:
        if (r10 != r1) goto L48;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static BerTlv decode(byte[] bArr) throws ResultException {
        int i;
        int i2;
        int i3;
        int length = bArr.length;
        boolean z = false;
        int i4 = 1;
        r3 = true;
        boolean z2 = true;
        try {
            try {
                int i5 = bArr[0] & 255;
                if (i5 == 208) {
                    i2 = 2;
                    try {
                        i = bArr[1] & 255;
                        if (i >= 128) {
                            if (i == 129) {
                                try {
                                    int i6 = bArr[2] & 255;
                                    if (i6 < 128) {
                                        throw new ResultException(ResultCode.CMD_DATA_NOT_UNDERSTOOD, "length < 0x80 length=" + Integer.toHexString(0) + " curIndex=3 endIndex=" + length);
                                    }
                                    i = i6;
                                    i2 = 3;
                                } catch (IndexOutOfBoundsException unused) {
                                    i4 = 3;
                                    throw new ResultException(ResultCode.REQUIRED_VALUES_MISSING, "IndexOutOfBoundsException  curIndex=" + i4 + " endIndex=" + length);
                                }
                            } else {
                                throw new ResultException(ResultCode.CMD_DATA_NOT_UNDERSTOOD, "Expected first byte to be length or a length tag and < 0x81 byte= " + Integer.toHexString(i) + " curIndex=2 endIndex=" + length);
                            }
                        }
                    } catch (IndexOutOfBoundsException unused2) {
                        i4 = 2;
                    }
                } else if (ComprehensionTlvTag.COMMAND_DETAILS.value() == (i5 & (-129))) {
                    i5 = 0;
                    i2 = 0;
                    i = 0;
                } else {
                    i = 0;
                    i2 = 1;
                }
                if (length - i2 < i) {
                    throw new ResultException(ResultCode.CMD_DATA_NOT_UNDERSTOOD, "Command had extra data endIndex=" + length + " curIndex=" + i2 + " length=" + i);
                }
                List<ComprehensionTlv> decodeMany = ComprehensionTlv.decodeMany(bArr, i2);
                if (i5 == 208) {
                    int i7 = 0;
                    for (ComprehensionTlv comprehensionTlv : decodeMany) {
                        int length2 = comprehensionTlv.getLength();
                        if (length2 >= 128 && length2 <= 255) {
                            i3 = length2 + 3;
                        } else if (length2 < 0 || length2 >= 128) {
                            z2 = false;
                            break;
                        } else {
                            i3 = length2 + 2;
                        }
                        i7 += i3;
                    }
                }
                z = z2;
                return new BerTlv(i5, decodeMany, z);
            } catch (IndexOutOfBoundsException unused3) {
            }
        } catch (ResultException e) {
            throw new ResultException(ResultCode.CMD_DATA_NOT_UNDERSTOOD, e.explanation());
        }
    }
}
