package com.android.internal.telephony.gsm;

import android.compat.annotation.UnsupportedAppUsage;
/* loaded from: classes.dex */
public class SimTlv {
    int mCurDataLength;
    int mCurDataOffset;
    int mCurOffset;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    boolean mHasValidTlvObject = parseCurrentTlvObject();
    byte[] mRecord;
    int mTlvLength;
    int mTlvOffset;

    @UnsupportedAppUsage
    public SimTlv(byte[] bArr, int i, int i2) {
        this.mRecord = bArr;
        this.mTlvOffset = i;
        this.mTlvLength = i2;
        this.mCurOffset = i;
    }

    @UnsupportedAppUsage
    public boolean nextObject() {
        if (this.mHasValidTlvObject) {
            this.mCurOffset = this.mCurDataOffset + this.mCurDataLength;
            boolean parseCurrentTlvObject = parseCurrentTlvObject();
            this.mHasValidTlvObject = parseCurrentTlvObject;
            return parseCurrentTlvObject;
        }
        return false;
    }

    @UnsupportedAppUsage
    public boolean isValidObject() {
        return this.mHasValidTlvObject;
    }

    @UnsupportedAppUsage
    public int getTag() {
        if (this.mHasValidTlvObject) {
            return this.mRecord[this.mCurOffset] & 255;
        }
        return 0;
    }

    @UnsupportedAppUsage
    public byte[] getData() {
        if (this.mHasValidTlvObject) {
            int i = this.mCurDataLength;
            byte[] bArr = new byte[i];
            System.arraycopy(this.mRecord, this.mCurDataOffset, bArr, 0, i);
            return bArr;
        }
        return null;
    }

    private boolean parseCurrentTlvObject() {
        try {
            byte[] bArr = this.mRecord;
            int i = this.mCurOffset;
            byte b = bArr[i];
            if (b != 0 && (b & 255) != 255) {
                if ((bArr[i + 1] & 255) < 128) {
                    this.mCurDataLength = bArr[i + 1] & 255;
                    this.mCurDataOffset = i + 2;
                } else if ((bArr[i + 1] & 255) == 129) {
                    this.mCurDataLength = bArr[i + 2] & 255;
                    this.mCurDataOffset = i + 3;
                }
                return this.mCurDataLength + this.mCurDataOffset <= this.mTlvOffset + this.mTlvLength;
            }
        } catch (ArrayIndexOutOfBoundsException unused) {
        }
        return false;
    }
}
