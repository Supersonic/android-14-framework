package com.android.internal.telephony.cat;

import com.android.internal.telephony.EncodeException;
import com.android.internal.telephony.GsmAlphabet;
import com.android.internal.telephony.PhoneConfigurationManager;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
/* compiled from: ResponseData.java */
/* loaded from: classes.dex */
class GetInkeyInputResponseData extends ResponseData {
    protected static final byte GET_INKEY_NO = 0;
    protected static final byte GET_INKEY_YES = 1;
    public String mInData;
    private boolean mIsPacked;
    private boolean mIsUcs2;
    private boolean mIsYesNo;
    private boolean mYesNoResponse;

    public GetInkeyInputResponseData(String str, boolean z, boolean z2) {
        this.mIsUcs2 = z;
        this.mIsPacked = z2;
        this.mInData = str;
        this.mIsYesNo = false;
    }

    public GetInkeyInputResponseData(boolean z) {
        this.mIsUcs2 = false;
        this.mIsPacked = false;
        this.mInData = PhoneConfigurationManager.SSSS;
        this.mIsYesNo = true;
        this.mYesNoResponse = z;
    }

    @Override // com.android.internal.telephony.cat.ResponseData
    public void format(ByteArrayOutputStream byteArrayOutputStream) {
        byte[] bArr;
        if (byteArrayOutputStream == null) {
            return;
        }
        byteArrayOutputStream.write(ComprehensionTlvTag.TEXT_STRING.value() | 128);
        if (this.mIsYesNo) {
            bArr = new byte[]{this.mYesNoResponse};
        } else {
            String str = this.mInData;
            if (str == null || str.length() <= 0) {
                bArr = new byte[0];
            } else {
                try {
                    if (this.mIsUcs2) {
                        bArr = this.mInData.getBytes("UTF-16BE");
                    } else if (this.mIsPacked) {
                        byte[] stringToGsm7BitPacked = GsmAlphabet.stringToGsm7BitPacked(this.mInData, 0, 0);
                        byte[] bArr2 = new byte[stringToGsm7BitPacked.length - 1];
                        System.arraycopy(stringToGsm7BitPacked, 1, bArr2, 0, stringToGsm7BitPacked.length - 1);
                        bArr = bArr2;
                    } else {
                        bArr = GsmAlphabet.stringToGsm8BitPacked(this.mInData);
                    }
                } catch (UnsupportedEncodingException unused) {
                    bArr = new byte[0];
                } catch (EncodeException unused2) {
                    bArr = new byte[0];
                }
            }
        }
        if (bArr.length + 1 <= 255) {
            ResponseData.writeLength(byteArrayOutputStream, bArr.length + 1);
        } else {
            bArr = new byte[0];
        }
        if (this.mIsUcs2) {
            byteArrayOutputStream.write(8);
        } else if (this.mIsPacked) {
            byteArrayOutputStream.write(0);
        } else {
            byteArrayOutputStream.write(4);
        }
        for (byte b : bArr) {
            byteArrayOutputStream.write(b);
        }
    }
}
